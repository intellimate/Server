package org.intellimate.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.error.ServerErrorHandler;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.registry.Registry;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;

import java.util.function.Consumer;

/**
 * @author LeanderK
 * @version 1.0
 */
public class RatpackRouter {
    private static final Logger logger = LoggerFactory.getLogger(RatpackRouter.class);
    private final MessageRenderer messageRenderer = new MessageRenderer();
    private final ErrorHandler errorHandler = new ErrorHandler();
    private final JWTHelper jwtHelper;
    private final int port;

    /**
     * creates a new Router.
     * @param jwtHelper the jwt-helper to use
     * @param port the port the server is listening on
     */
    public RatpackRouter(JWTHelper jwtHelper, int port) {
        this.jwtHelper = jwtHelper;
        this.port = port;
    }

    public void init() throws Exception {
        Registry registry = Registry.builder()
                .add(messageRenderer)
                .add(ServerErrorHandler.class, errorHandler)
                .build();
        RatpackServer.start(server -> server
                .serverConfig(ServerConfig.embedded().port(port))
                .registry(registry)
                .handlers(chain -> chain
                        .all(ctx -> {
                            ctx.getResponse().getHeaders().add("access-control-allow-origin", "*");
                            ctx.getResponse().getHeaders().add("access-control-allow-methods", "GET,PUT,POST,PATCH,DELETE,OPTIONS");
                            ctx.getResponse().getHeaders().add("access-control-allow-credentials", "true");
                            ctx.getResponse().getHeaders().add("access-control-allow-headers", "Authorization,Content-Type");
                            ctx.getResponse().getHeaders().add("access-control-expose-headers", "Link,Location");
                            ctx.getResponse().getHeaders().add("access-control-max-age", "86400");
                            String jwtHeader = ctx.getRequest().getHeaders().get("Authorization");
                            if (jwtHeader == null) {
                                jwtHeader = ctx.getRequest().getQueryParams().get("Authorization");
                            }
                            if (jwtHeader != null) {
                                if (!jwtHeader.matches("Bearer .*")) {
                                    throw new BadRequestException("Authorization header must contain Bearer token in the format <Bearer JWT>");
                                }
                                String jwt = jwtHeader.substring("Bearer ".length());
                                //String workerID = jwtHelper.getSubject(jwt);
                                //ctx.next(Registry.single(WorkerID.class, new WorkerID(workerID)));
                            } else {
                                ctx.next();
                            }
                        })
                        .options(ctx -> {
                            ctx.getResponse().status(204);
                            ctx.getResponse().contentType("text/plain");
                            ctx.render("");
                        })
                        //.get("experiments/:platform", ctx -> ctx.render(queries.getExperiments(ctx)))
                        //.get("preview/:experiment", ctx -> ctx.render(queries.preview(ctx)))
                        //.get("next/:platform/:experiment", ctx -> ctx.render(queries.getNext(ctx)))
                        //.post("emails/:platform", ctx -> ctx.render(commands.submitEmail(ctx)))
                        //.post("answers", doAuthorized(ctx -> ctx.render(commands.submitAnswer(ctx))))
                        //.post("ratings", doAuthorized(ctx -> ctx.render(commands.submitRating(ctx))))
                        //.post("calibrations", doAuthorized(ctx -> ctx.render(commands.submitCalibration(ctx))))
                        //.post("delete", doAuthorized(ctx -> ctx.render(commands.deleteWorker(ctx))))
                )
        );
    }

    /**
     * executes the consumer if the client is authorized
     * @param contextConsumer the consumer to execute
     * @return a handler that executes the consumer if authorized
     */
    private Handler doAuthorized(Consumer<Context> contextConsumer) {
        return ctx -> {
            //ctx.maybeGet(WorkerID.class)
            //        .orElseThrow(() -> new UnauthorizedException("Client needs the Authorization-header to acces the method"));
            contextConsumer.accept(ctx);
        };
    }
}
