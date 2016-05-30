package org.intellimate.server.izou;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.intellimate.server.*;
import org.intellimate.server.database.model.tables.records.IzouInstanceRecord;
import org.intellimate.server.database.operations.IzouInstanceOperations;
import org.intellimate.server.jwt.JWTHelper;
import org.intellimate.server.jwt.JWTokenPassed;
import org.intellimate.server.jwt.Subject;
import org.intellimate.server.proto.HttpRequest;
import org.intellimate.server.proto.HttpResponse;
import org.intellimate.server.proto.SocketConnection;
import org.intellimate.server.proto.SocketConnectionResponse;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.stream.internal.BufferingPublisher;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * this class holds all the open izou-connections
 * @author LeanderK
 * @version 1.0
 */
//TODO timeout? (I think it is already working, but needs to be verified)
public class Communication implements RequestHelper {
    private final ConcurrentMap<Integer, IzouConnection> izouConnections = new ConcurrentHashMap<>();
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
        String path = uri.replace(String.format("users/%d/izou/%d/instance/", userID, izouId), "");
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
        jwt.getApp().ifPresent(ignored ->
                params.add(HttpRequest.Param.newBuilder().setKey("token").addValue(jwt.getToken()).build())
        );

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .setUrl(path);
        String contentType = context.getRequest().getContentType().getType();
        if (contentType != null) {
            builder.setContentType(contentType);
        }

        HttpRequest httpRequest = builder
                .setMethod(context.getRequest().getMethod().getName())
                .setBodySize((int) context.getRequest().getContentLength())
                .addAllParams(
                        params
                )
                .build();
        communicate(httpRequest, izouId, context);
    }

    public void startServer() throws IOException {
        if (!sslEnabled) {
            serverSocket = new ServerSocket(4000, 0, InetAddress.getByName("localhost"));
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

    private void handleSocket(Socket socket) {
        int izouId = -1;
        try {
            InputStream inputStream = null;
            inputStream = socket.getInputStream();
            SocketConnection socketConnection = SocketConnection.parseDelimitedFrom(inputStream);
            if (socketConnection == null) {
                logger.debug("unable to parse SocketConnection");
                try {
                    socket.close();
                } catch (IOException ex) {
                    logger.error("an error occured while trying to close socket", ex);
                }
                return;
            }
            JWTokenPassed jwTokenPassed = jwtHelper.parseToken(socketConnection.getToken());
            if (!jwTokenPassed.getSubject().equals(Subject.IZOU)) {
                logger.error("connection tried with illegal token");
                try {
                    socket.close();
                } catch (IOException ex) {
                    logger.error("an error occured while trying to close socket", ex);
                }
                return;
            }
            izouId = jwTokenPassed.getId();
            Optional<IzouInstanceRecord> instance = izouInstanceOperations.getInstance(izouId);
            if (!instance.isPresent()) {
                logger.error("Izou-Instance is not existing anymore");
                try {
                    socket.close();
                } catch (IOException ex) {
                    logger.error("an error occured while trying to close socket", ex);
                }
                return;
            } else {
                SocketConnectionResponse.newBuilder()
                        .setId(instance.get().getIdInstances())
                        .setRoute("/users/"+instance.get().getUser()+"/izou/"+instance.get().getIdInstances()+"/instance/")
                        .build()
                        .writeDelimitedTo(socket.getOutputStream());
            }
            IzouConnection izouConnection = new IzouConnection(socket, new BoundedLock(30));
            IzouConnection existingMayBeClosed = izouConnections.get(izouId);
            if (existingMayBeClosed != null && existingMayBeClosed.socket.isClosed()) {
                izouConnections.remove(izouId);
            }
            IzouConnection existing = izouConnections.putIfAbsent(izouId, izouConnection);
            if (existing != null) {
                logger.debug("already connected "+izouId);
                try {
                    socket.close();
                } catch (IOException ex) {
                    logger.error("an error occured while trying to close socket", ex);
                }
            }
        } catch (Exception e) {
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

    private void communicate(HttpRequest httpRequest, int izou, Context context) {
        IzouConnection izouConnection = izouConnections.get(izou);
        if (izouConnection == null) {
            throw new NotFoundException("No connection to Izou");
        } else if (izouConnection.socket.isClosed()) {
            izouConnections.remove(izou);
            throw new NotFoundException("No connection to Izou");
        } else {
            Blocking.get(() -> {
                BoundedLock.LockHolder lockHolder;
                try {
                    lockHolder = izouConnection.lock.lock(10, TimeUnit.SECONDS)
                            .orElseThrow(() -> new InternalServerErrorException("1. Izou Connection is busy"));
                } catch (IllegalStateException e) {
                    throw new BadRequestException("Maximum of parallel Requests for Izou-Instance reached");
                }
                try {
                    if (izouConnection.socket.isClosed() || !(izouConnections.get(izou) == izouConnection)) {
                        throw new InternalServerErrorException("1. Izou Connection closed while waiting for availability");
                    }
                    InputStream in = izouConnection.socket.getInputStream();
                    OutputStream out = izouConnection.socket.getOutputStream();
                    // Write the response to the wire
                    httpRequest.writeDelimitedTo(out);
                    if (httpRequest.getBodySize() != -1) {
                        Long written = Blocking.on(Promise.async(down -> down.accept(writeBody(out, context, httpRequest.getBodySize()))));
                        if (httpRequest.getBodySize() != written) {
                            throw new IzouCommunicationException("Actual Body length does not match content-lenght header");
                        }
                    }
                    out.flush();

                    HttpResponse response = HttpResponse.parseDelimitedFrom(in);
                    if (response == null) {
                        throw new IzouCommunicationException("Unable to read response from izou");
                    }
                    return new Response(response, izouConnection.socket, lockHolder);
                } catch (IzouCommunicationException | SocketException e) {
                    izouConnection.socket.close();
                    izouConnections.remove(izou);
                    throw e;
                } finally {
                    lockHolder.unlock();
                }
            }).then(response -> sendResponse(response.httpResponse, response.socket, context, response.lock));

        }
    }

    private CompletableFuture<Long> writeBody(final OutputStream out, Context context, long size) {
        CompletableFuture<Long> result = new CompletableFuture<>();
        WritableByteChannel channel = Channels.newChannel(out);

        context.getRequest().getBodyStream(size).subscribe(new Subscriber<ByteBuf>() {
            private Subscription subscription;
            long written = 0;
            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                subscription.request(1);
            }

            @Override
            public void onNext(ByteBuf byteBuf) {
                Blocking.get(() -> channel.write(byteBuf.nioBuffer()))
                        .onError(error -> {
                            byteBuf.release();
                            subscription.cancel();
                            out.close();
                            result.completeExceptionally(new IzouCommunicationException("unable to write into outpustream", error));
                        }).then(bytesWritten -> {
                            byteBuf.release();
                            written += bytesWritten;
                            subscription.request(1);
                        });
            }

            @Override
            public void onError(Throwable t) {
                result.completeExceptionally(new IzouCommunicationException("an error occurred while trying to write the body", t));
            }

            @Override
            public void onComplete() {
                result.complete(written);
            }
        });
        return result;
    }

    private void sendResponse(HttpResponse response, Socket socket, Context context, BoundedLock.LockHolder lock) {
        context.getResponse().status(response.getStatus());
        response.getHeadersList()
                .forEach(header -> context.getResponse().getHeaders().set(header.getKey(), header.getValueList()));
        if (!response.getContentType().equals("")) {
            context.getResponse().contentType(response.getContentType());
        }

        InputStream input = null;
        try {
            input = socket.getInputStream();
        } catch (IOException e) {
            serverError(context, socket, lock, new IzouCommunicationException("unable to obtain inputStream", e));
        }
        InputStream in = input;

        long bodySize = response.getBodySize();
        if (bodySize < 0) {
            serverError(context, socket, lock, new IzouCommunicationException("Izou returned an illegal body-size"));
        }
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
                                            lock.unlock();
                                            write.complete();
                                        } else {
                                            IzouCommunicationException exception = new IzouCommunicationException("Body size does not match advertised-size");
                                            Blocking.op(() -> serverError(context, socket, lock, exception))
                                                    .then(write::complete);
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
                    int read = in.read(buffer);
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
                            .onError(t -> {
                                IzouCommunicationException izouCommunicationException = new IzouCommunicationException("unable to consume inputStream fully", t);
                                serverError(context, socket, lock, izouCommunicationException);
                            })
                            .then(lock::unlock);
                }
            };
        }));
    }

    private void serverError(Context context, Socket socket, BoundedLock.LockHolder lock, Throwable throwable) {
        try {
            socket.close();
        } catch (IOException e) {
            //ignored
        }
        lock.unlock();
        context.error(throwable);
    }

    private static class Response {
        final HttpResponse httpResponse;
        final Socket socket;
        final BoundedLock.LockHolder lock;

        private Response(HttpResponse httpResponse, Socket socket, BoundedLock.LockHolder lock) {
            this.httpResponse = httpResponse;
            this.socket = socket;
            this.lock = lock;
        }
    }

    private static class IzouConnection {
        final Socket socket;
        final BoundedLock lock;

        private IzouConnection(Socket socket, BoundedLock lock) {
            this.socket = socket;
            this.lock = lock;
        }
    }
}
