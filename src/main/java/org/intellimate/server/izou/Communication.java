package org.intellimate.server.izou;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.intellimate.server.InternalServerErrorException;
import org.intellimate.server.NotFoundException;
import org.intellimate.server.RequestHelper;
import org.intellimate.server.UnauthorizedException;
import org.intellimate.server.database.operations.IzouInstanceOperations;
import org.intellimate.server.jwt.JWTHelper;
import org.intellimate.server.jwt.JWTokenPassed;
import org.intellimate.server.jwt.Subject;
import org.intellimate.server.proto.HttpRequest;
import org.intellimate.server.proto.HttpResponse;
import org.intellimate.server.proto.SocketConnection;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;
import ratpack.func.Action;
import ratpack.handling.Context;
import ratpack.http.ResponseChunks;
import ratpack.stream.TransformablePublisher;
import ratpack.stream.internal.BufferingPublisher;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * this class holds all the open izou-connections
 * @author LeanderK
 * @version 1.0
 */
public class Communication implements RequestHelper {
    private final ConcurrentMap<Integer, BlockingDeque<Command>> izouConnections = new ConcurrentHashMap<>();
    private static Logger logger = LoggerFactory.getLogger(Communication.class);
    private boolean run = true;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private ServerSocket serverSocket;
    private final JWTHelper jwtHelper;
    private final IzouInstanceOperations izouInstanceOperations;
    private final boolean sslEnabled;

    public Communication(JWTHelper jwtHelper, IzouInstanceOperations izouInstanceOperations, boolean sslEnabled) {
        this.jwtHelper = jwtHelper;
        this.izouInstanceOperations = izouInstanceOperations;
        this.sslEnabled = sslEnabled;
    }

    public void handleRequest(Context context) {
        String uri = context.getRequest().getUri();
        int userID = assertParameterInt(context, "id");
        int izouId = assertParameterInt(context, "izouId");
        JWTokenPassed jwt = context.get(JWTokenPassed.class);
        if (jwt.getSubject().equals(Subject.IZOU) && jwt.getId() != izouId) {
            throw new UnauthorizedException("not authorized for izou instance "+izouId);
        }
        if (jwt.getSubject().equals(Subject.USER)) {
            boolean valid = izouInstanceOperations.validateIzouInstanceID(izouId, jwt.getId());
            if (!valid) {
                throw new UnauthorizedException("not authorized for izou instance "+izouId);
            }
        }
        if (!jwt.getApp().isPresent()) {
            throw new UnauthorizedException("app indentifier missing");
        }
        String path = uri.replace(String.format("users/%d/izou/%d", userID, izouId), "");
        List<HttpRequest.Param> params = context.getRequest().getQueryParams().getAll().entrySet().stream()
                .filter(entry -> !(entry.getKey().equals("user") || entry.getKey().equals("izou") || entry.getKey().equals("app")))
                .map(entry -> HttpRequest.Param.newBuilder()
                        .setKey(entry.getKey())
                        .addAllValue(entry.getValue())
                        .build()
                )
                .collect(Collectors.toList());
        params.add(HttpRequest.Param.newBuilder().setKey("user").addValue(String.valueOf(userID)).build());
        params.add(HttpRequest.Param.newBuilder().setKey("izou").addValue(String.valueOf(izouId)).build());
        jwt.getApp().ifPresent(id -> params.add(HttpRequest.Param.newBuilder().setKey("app").addValue(id).build()));
        context.getRequest().getBody()
                .map(data -> HttpRequest.newBuilder()
                        .setUrl(path)
                        .setContentType(context.getRequest().getContentType().getType())
                        .setMethod(context.getRequest().getMethod().getName())
                        .setBodySize((int) context.getRequest().getContentLength())
                        .addAllParams(
                                params
                        )
                        .build()
                )
                .flatMap(httpRequest -> {
                    TransformablePublisher<? extends ByteBuf> bodyStream = null;
                    if (httpRequest.getBodySize() != -1) {
                        bodyStream = context.getRequest().getBodyStream(httpRequest.getBodySize());
                    }
                    return communicate(httpRequest, bodyStream, izouId);
                })
                .then(response -> sendResponse(response, context));
    }

    public void startServer() throws IOException {
        if (!sslEnabled) {
            serverSocket = new ServerSocket(4000);
        } else {
            System.setProperty("javax.net.ssl.keyStore", "../keystore.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "XL750BK");
            ServerSocketFactory socketFactory = SSLServerSocketFactory.getDefault();
            serverSocket = socketFactory.createServerSocket(4000);
        }
        executorService.execute(() -> {
            while (run) {
                try {
                    Socket accept = serverSocket.accept();
                    handleSocket(accept);
                } catch (IOException e) {
                    logger.error("unable to accept socket", e);
                }
                if (serverSocket.isClosed())
                    return;
            }
        });
    }

    private void sendResponse(Response response, Context context) {
        context.getResponse().status(response.response.getStatus());
        response.response.getHeadersList()
                .forEach(header -> context.getResponse().getHeaders().set(header.getKey(), header.getValueList()));
        if (!response.response.getContentType().equals("")) {
            context.getResponse().contentType(response.response.getContentType());
        }

        long bodySize = response.response.getBodySize();
        context.getResponse().getHeaders().set("Content-Length", bodySize);
        int bufferSize = 8192;
        context.getResponse().sendStream(new BufferingPublisher<>(ByteBuf::release, write -> {
            return new Subscription() {
                boolean cancelled;
                long bytesRead = 0;

                @Override
                public void request(long n) {
                    emit();
                }

                private void emit() {
                    Blocking.get(this::read)
                            .onError(write::error)
                            .then(b -> {
                                if (!cancelled) {
                                    if (b == null) {
                                        if (bytesRead == bodySize) {
                                            response.responseFinished.complete(bytesRead);
                                            write.complete();
                                        } else {
                                            EOFException exception = new EOFException("Body size does not match advertised-size");
                                            response.responseFinished.completeExceptionally(exception);
                                            write.error(exception);
                                        }
                                    } else {
                                        write.item(b);
                                        if (write.getRequested() > 0) {
                                            emit();
                                        }
                                    }
                                }
                            });
                }

                private ByteBuf read() throws IOException {
                    int toRead = (int) Math.min(bufferSize, bodySize - bytesRead);
                    byte[] buffer = new byte[toRead];
                    int read = response.stream.read(buffer);
                    if (read == -1) {
                        return null;
                    } else {
                        bytesRead += read;
                        return Unpooled.wrappedBuffer(buffer, 0, read);
                    }
                }

                @Override
                public void cancel() {
                    cancelled = true;
                    Blocking.op(() -> {
                                while (bytesRead != bodySize) {
                                    read();
                                }
                            })
                            .onError(this::communicateError)
                            .then();
                }

                private void communicateError(Throwable throwable) {
                    response.responseFinished.completeExceptionally(throwable);
                }
            };
        }));
    }

    private void handleSocket(Socket socket) {
        int izouId = -1;
        Future<?> submit = null;
        try {
            InputStream inputStream = null;inputStream = socket.getInputStream();
            SocketConnection socketConnection = SocketConnection.parseDelimitedFrom(inputStream);
            JWTokenPassed jwTokenPassed = jwtHelper.parseToken(socketConnection.getToken());
            if (!jwTokenPassed.getSubject().equals(Subject.IZOU)) {
                logger.error("connection tried with illegal token");
                return;
            }
            izouId = jwTokenPassed.getId();
            if (izouConnections.containsKey(izouId)) {
                logger.error("already connected "+izouId);
                try {
                    socket.close();
                } catch (IOException ex) {
                    logger.error("an error occured while trying to close socket", ex);
                }
            } else {
                int finalIzouId = izouId;
                LinkedBlockingDeque<Command> commands = new LinkedBlockingDeque<>();
                izouConnections.put(izouId, commands);
                InputStream finalInputStream = inputStream;
                submit = executorService.submit(() -> communicateWithIzou(commands, finalIzouId, socket, finalInputStream));
            }
        } catch (IOException e) {
            logger.error("an error occured while trying to close socket", e);
            if (izouId != -1) {
                izouConnections.remove(izouId);
            }
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    logger.error("an error occured while trying to close socket", ex);
                }
            }
        }

    }

    private Promise<Response> communicate(HttpRequest httpRequest, TransformablePublisher<? extends ByteBuf> bodyStream, int izou) {
        BlockingDeque<Command> queue = izouConnections.get(izou);
        if (queue == null) {
            throw new NotFoundException("1: No connection to Izou");
        } else {
            Command command = new Command(httpRequest, bodyStream);
            try {
                queue.put(command);
            } catch (InterruptedException e) {
                throw new InternalServerErrorException("Izou Connection is busy");
            }
            return Promise.of(downstream -> downstream.accept(command.response));
        }
    }

    private void communicateWithIzou(BlockingDeque<Command> input, int izouID, Socket socket, InputStream in) {
        try {
            try {
                OutputStream out = socket.getOutputStream();
                boolean loop = true;
                while (run && loop) {
                    try {
                        Command command = input.poll(30, TimeUnit.SECONDS);
                        if (command == null) {
                            if (!socket.isConnected()) {
                                loop = false;
                            }
                            continue;
                        }
                        // Write the response to the wire
                        command.request.writeDelimitedTo(out);
                        Lock waitLock = new ReentrantLock();
                        Condition waitCondition = waitLock.newCondition();
                        final long[] written = {0};
                        if (command.request.getBodySize() == -1L) {
                            WritableByteChannel channel = Channels.newChannel(out);
                            command.bodyStream.subscribe(new Subscriber<ByteBuf>() {
                                private Subscription subscription;
                                @Override
                                public void onSubscribe(Subscription s) {
                                    subscription = s;
                                }

                                @Override
                                public void onNext(ByteBuf byteBuf) {
                                    Blocking.get(() -> channel.write(byteBuf.nioBuffer()))
                                            .onError(error -> {
                                                byteBuf.release();
                                                subscription.cancel();
                                                out.close();
                                                command.response.completeExceptionally(new InternalServerErrorException("3: unable to write into outpustream", error));
                                            }).then(bytesWritten -> {
                                                byteBuf.release();
                                                written[0] += bytesWritten;
                                                subscription.request(1);
                                            });
                                }

                                @Override
                                public void onError(Throwable t) {
                                    command.response.completeExceptionally(new InternalServerErrorException("2: unable to read the body of the request"));
                                    waitCondition.signalAll();
                                }

                                @Override
                                public void onComplete() {
                                    waitCondition.signalAll();
                                }
                            });
                        }
                        waitCondition.await();
                        if (command.response.isCompletedExceptionally()) {
                            command.response.join();
                        }
                        if (command.request.getBodySize() != -1) {
                            if (command.request.getBodySize() != written[0]) {
                                command.response.completeExceptionally(new InternalServerErrorException("4: Actual Body length does not match content-lenght header"));
                                loop = false;
                                continue;
                            }
                        }
                        out.flush();

                        HttpResponse response = HttpResponse.parseDelimitedFrom(in);
                        if (response == null) {
                            loop = false;
                        }
                        Response responseFinal = new Response(response, in);
                        command.response.complete(responseFinal);
                        responseFinal.responseFinished.join();
                    } catch (InterruptedException e) {
                        if (!socket.isConnected()) {
                            loop = false;
                        }
                    }
                }
                in.close();
                out.close();
            } catch (IOException e) {
                logger.error("unable to operate on streams", e);
            }
        } finally {
            izouConnections.remove(izouID);
            input.forEach(command -> command.response.completeExceptionally(new NotFoundException("1: No connection to Izou")));
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error("unable to close connection", e);
                }
            }
        }
    }

    private static class Command {
        final HttpRequest request;
        final TransformablePublisher<? extends ByteBuf> bodyStream;
        final CompletableFuture<Response> response;

        Command(HttpRequest request, TransformablePublisher<? extends ByteBuf> bodyStream) {
            this.request = request;
            this.bodyStream = bodyStream;
            response = new CompletableFuture<>();
        }
    }

    private static class Response {
        final HttpResponse response;
        final InputStream stream;
        final CompletableFuture<Long> responseFinished;

        private Response(HttpResponse response, InputStream stream) {
            this.response = response;
            this.stream = stream;
            responseFinished = new CompletableFuture<>();
        }
    }
}
