package org.intellimate.server.izou;
import com.google.protobuf.ByteString;
import org.intellimate.server.InternalServerErrorException;
import org.intellimate.server.NotFoundException;
import org.intellimate.server.RequestHelper;
import org.intellimate.server.UnauthorizedException;
import org.intellimate.server.jwt.JWTHelper;
import org.intellimate.server.jwt.JWTokenPassed;
import org.intellimate.server.jwt.Subject;
import org.intellimate.server.proto.HttpRequest;
import org.intellimate.server.proto.HttpResponse;
import org.intellimate.server.proto.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Promise;
import ratpack.handling.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * this class holds all the open izou-connections
 * @author LeanderK
 * @version 1.0
 */
public class Communication implements RequestHelper {
    private final HashMap<Integer, BlockingDeque<Command>> izouConnections = new HashMap<>();
    private static Logger logger = LoggerFactory.getLogger(Communication.class);
    private boolean run = true;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private ServerSocket serverSocket;
    private final JWTHelper jwtHelper;

    public Communication(JWTHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
    }

    public Promise<HttpResponse> handleRequest(Context context) {
        String uri = context.getRequest().getUri();
        int userID = assertParameterInt(context, "id");
        int izouId = assertParameterInt(context, "izouId");
        JWTokenPassed jwt = context.get(JWTokenPassed.class);
        if (jwt.getId() != izouId) {
            throw new UnauthorizedException("not authorized for izou instance "+izouId);
        }
        String path = uri.replace(String.format("users/%d/izou/%d", userID, izouId), "");
        return context.getRequest().getBody()
                .map(data ->
                        HttpRequest.newBuilder()
                                .setUrl(path)
                                .setContentType(context.getRequest().getContentType().getType())
                                .setMethod(context.getRequest().getMethod().getName())
                                .setBody(ByteString.copyFrom(data.getBytes()))
                                .addAllParams(
                                        context.getRequest().getQueryParams().getAll().entrySet().stream()
                                        .map(entry -> HttpRequest.Param.newBuilder()
                                                .setKey(entry.getKey())
                                                .addAllValue(entry.getValue())
                                                .build()
                                        )
                                        .collect(Collectors.toList())
                                )
                                .build()
                )
                .flatMap(httpRequest -> communicate(httpRequest, izouId));
    }

    public void startServer() throws IOException {
        serverSocket = new ServerSocket(40);
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
        try {
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                logger.error("unable to get input stream", e);
                return;
            }
            try {
                SocketConnection socketConnection = SocketConnection.parseDelimitedFrom(inputStream);
                JWTokenPassed jwTokenPassed = jwtHelper.parseToken(socketConnection.getToken());
                if (!jwTokenPassed.getSubject().equals(Subject.IZOU)) {
                    logger.error("connection tried with illegal token");
                    return;
                }
                int izouId = jwTokenPassed.getId();
                if (izouConnections.containsKey(izouId)) {
                    logger.error("already connected "+izouId);
                    return;
                } else {
                    LinkedBlockingDeque<Command> commands = new LinkedBlockingDeque<>();
                    izouConnections.put(izouId, commands);
                    InputStream finalInputStream = inputStream;
                    executorService.submit(() -> communicateWithIzou(commands, socket, finalInputStream));
                }
            } catch (IOException e) {
                logger.error("unable to process input stream", e);
                return;
            }

        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("an error occured while trying to close socket", e);
            }
        }

    }

    private Promise<HttpResponse> communicate(HttpRequest httpRequest, int izou) {
        BlockingDeque<Command> queue = izouConnections.get(izou);
        if (queue == null) {
            throw new NotFoundException("1: No connection to Izou");
        } else {
            Command command = new Command(httpRequest);
            try {
                queue.put(command);
            } catch (InterruptedException e) {
                throw new InternalServerErrorException("Izou Connection is busy");
            }
            return Promise.of(downstream -> downstream.accept(command.response));
        }
    }

    void communicateWithIzou(BlockingDeque<Command> input, Socket socket, InputStream in) {
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
                        out.flush();

                        HttpResponse response = HttpResponse.parseDelimitedFrom(in);
                        if (response == null) {
                            loop = false;
                        }
                        command.response.complete(response);
                    } catch (InterruptedException e) {
                        if (!socket.isConnected()) {
                            loop = false;
                        }
                    }
                }
                input.forEach(command -> command.response.completeExceptionally(new NotFoundException("1: No connection to Izou")));
                in.close();
                out.close();
            } catch (IOException e) {
                logger.error("unable to open streams", e);
            }
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("unable to close connection", e);
            }
        }
    }

    private static class Command {
        final HttpRequest request;
        final CompletableFuture<HttpResponse> response;

        Command(HttpRequest request) {
            this.request = request;
            response = new CompletableFuture<>();
        }
    }
}