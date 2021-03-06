package org.intellimate.server;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.intellimate.server.izou.Communication;
import org.intellimate.server.jwt.JWTHelper;
import org.intellimate.server.jwt.JWTokenPassed;
import org.intellimate.server.jwt.Subject;
import org.intellimate.server.proto.App;
import org.intellimate.server.proto.IzouInstance;
import org.intellimate.server.proto.User;
import org.intellimate.server.rest.AppResource;
import org.intellimate.server.rest.Authentication;
import org.intellimate.server.rest.IzouResource;
import org.intellimate.server.rest.UsersResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.error.ServerErrorHandler;
import ratpack.exec.Promise;
import ratpack.handling.Chain;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.TypedData;
import ratpack.registry.Registry;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author LeanderK
 * @version 1.0
 */
//TODO maxsize
public class RatpackRouter implements RequestHelper {
    private static final Logger logger = LoggerFactory.getLogger(RatpackRouter.class);
    private final JsonFormat.Parser parser = JsonFormat.parser();
    private final MessageRenderer messageRenderer = new MessageRenderer();
    private final PaginatedRenderer paginatedRenderer = new PaginatedRenderer();
    private final ErrorHandler errorHandler = new ErrorHandler();
    private final JWTHelper jwtHelper;
    private final Authentication authentication;
    private final UsersResource usersResource;
    private final IzouResource izouResource;
    private final AppResource appResource;
    private final Communication communication;
    private final int port;
    private final String fileDir;

    /**
     * creates a new Router.
     *
     * @param jwtHelper      the jwt-helper to use
     * @param authentication
     * @param usersResource
     * @param izouResource
     * @param appResource
     * @param communication
     * @param port           the port the server is listening on
     * @param fileDir
     */
    public RatpackRouter(JWTHelper jwtHelper, Authentication authentication, UsersResource usersResource, IzouResource izouResource, AppResource appResource, Communication communication, int port, String fileDir) {
        this.jwtHelper = jwtHelper;
        this.authentication = authentication;
        this.usersResource = usersResource;
        this.izouResource = izouResource;
        this.appResource = appResource;
        this.communication = communication;
        this.port = port;
        this.fileDir = fileDir;
    }

    //TODO: correct handling of refreshIzou vs. auth tokens?
    public void init() throws Exception {
        Registry registry = Registry.builder()
                .add(messageRenderer)
                .add(paginatedRenderer)
                .add(ServerErrorHandler.class, errorHandler)
                .build();
        RatpackServer.start(server -> server
                .serverConfig(ServerConfig.embedded().port(port).baseDir(new File(fileDir).getAbsoluteFile().getCanonicalFile()))
                .registry(registry)
                .handlers(chain -> {
                            Chain put = chain
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
                                            JWTokenPassed jwTokenPassed = jwtHelper.parseToken(jwt);
                                            ctx.next(Registry.single(JWTokenPassed.class, jwTokenPassed));
                                        } else {
                                            ctx.next();
                                        }
                                    })
                                    .options(ctx -> {
                                        ctx.getResponse().status(204);
                                        ctx.getResponse().contentType("text/plain");
                                        ctx.render("");
                                    })
                                    //authentication
                                    .post("authentication/refreshIzou/izou", ctx -> ctx.render(
                                            authentication.refreshIzou(ctx.get(JWTokenPassed.class))
                                    ))
                                    .post("authentication/users", ctx -> ctx.render(
                                            merge(ctx, User.newBuilder(), Arrays.asList(User.ID_FIELD_NUMBER, User.USERNAME_FIELD_NUMBER))
                                                    .map(message -> authentication.login(message.getEmail(), message.getPassword()))
                                    ))
                                    .post("authentication/apps/:izouId/:app", assureUser(ctx -> ctx.render(
                                            authentication.app(
                                                    ctx.get(JWTokenPassed.class).getId(),
                                                    assertParameterInt(ctx, "izouId"),
                                                    assertParameter(ctx, "app"))
                                    )))
                                    //users
                                    .put("users", ctx -> ctx.render(
                                            merge(ctx, User.newBuilder(), Collections.singletonList(User.ID_FIELD_NUMBER))
                                                    .map(message -> usersResource.addUser(message.getUsername(), message.getEmail(), message.getPassword()))
                                    ))
                                    .path("users/:id", assureUser(ctx -> ctx
                                            .byMethod(m -> m
                                                    .get(() -> ctx.render(
                                                            usersResource.getUser(ctx.get(JWTokenPassed.class).getId(), assertParameterInt(ctx, "id"))
                                                    ))
                                                    .patch(() -> ctx.render(
                                                            merge(ctx, User.newBuilder(), Collections.singletonList(User.ID_FIELD_NUMBER))
                                                                    .map(message -> usersResource.patchUser(message.build(), ctx.get(JWTokenPassed.class).getId()))
                                                    ))
                                                    .delete(() -> ctx.render(
                                                            usersResource.removeUser(assertParameterInt(ctx, "id"), ctx.get(JWTokenPassed.class))
                                                    ))
                                            )
                                    ))
                                    .path("users/:id/izou", assureUser(ctx -> ctx
                                            .byMethod(m -> m
                                                    .get(() -> ctx.render(
                                                            usersResource.getIzouInstances(ctx.get(JWTokenPassed.class).getId())
                                                    ))
                                                    .put(() -> ctx.render(
                                                            merge(ctx, IzouInstance.newBuilder(), Arrays.asList(IzouInstance.ID_FIELD_NUMBER, IzouInstance.TOKEN_FIELD_NUMBER))
                                                                    .map(message -> usersResource.addIzouInstance(assertParameterInt(ctx, "id"), message.getName(), ctx.get(JWTokenPassed.class)))
                                                    ))

                                            )
                                    ))
                                    .path("users/:id/izou/:izouid", assureUser(ctx -> ctx
                                            .byMethod(m -> m
                                                    .get(() -> ctx.render(
                                                            usersResource.getIzouInstance(ctx.get(JWTokenPassed.class).getId(), assertParameterInt(ctx, "izouid"))
                                                    ))
                                                    .delete(() -> ctx.render(
                                                            usersResource.removeIzouInstance(assertParameterInt(ctx, "id"), assertParameterInt(ctx, "izouid"), ctx.get(JWTokenPassed.class))
                                                    ))
                                            )
                                    ))
                                    .get("users/:id/confirm/:token", ctx -> ctx.render(
                                            usersResource.confirmUser(jwtHelper.parseToken(assertParameter(ctx, "token")))
                                    ))
                                    //temporary until website
                                    .get("users/:id/reset-password/reset/:token/:pass", ctx -> ctx.render(
                                            usersResource.resetPassword(jwtHelper.parseToken(assertParameter(ctx, "token")), assertParameter(ctx, "pass"))
                                    ))
                                    .get("users/:id/apps", assureUser(ctx -> ctx.render(
                                            usersResource.getUsersApps(ctx.get(JWTokenPassed.class).getId())
                                    )))
                                    .put("users/:id/reset-password/reset/", ctx -> {
                                        JWTokenPassed token = ctx.maybeGet(JWTokenPassed.class)
                                                .orElseThrow(() -> new UnauthorizedException("Request must be authenticated"));
                                        ctx.render(
                                                merge(ctx, User.newBuilder(), Arrays.asList(User.ID_FIELD_NUMBER, User.USERNAME_FIELD_NUMBER, User.EMAIL_FIELD_NUMBER))
                                                        .map(message -> usersResource.resetPassword(token, message.getPassword())))
                                        ;
                                    })
                                    .put("users/resend-confirmation", ctx -> ctx.render(
                                            merge(ctx, User.newBuilder(), Arrays.asList(User.ID_FIELD_NUMBER, User.USERNAME_FIELD_NUMBER, User.PASSWORD_FIELD_NUMBER))
                                                    .map(message -> usersResource.resendConfirmationEmail(message.getEmail()))
                                    ))
                                    .put("users/reset-password/init", ctx -> ctx.render(
                                            merge(ctx, User.newBuilder(), Arrays.asList(User.ID_FIELD_NUMBER, User.USERNAME_FIELD_NUMBER, User.PASSWORD_FIELD_NUMBER))
                                                    .map(message -> usersResource.sendPasswordResetEmail(message.getEmail()))
                                    ))
                                    .prefix("users/:id/izou/:izouId/instance/:command", chain2 -> chain2.all(communication::handleRequest))
                                    //izou
                                    .get("izou", ctx -> ctx.render(izouResource.getCurrentVersion()))
                                    .put("izou/:major/:minor/:patch", assureUser(ctx -> ctx.render(
                                            izouResource.putIzou(
                                                    assertParameterInt(ctx, "major"),
                                                    assertParameterInt(ctx, "minor"),
                                                    assertParameterInt(ctx, "patch"),
                                                    ctx.get(JWTokenPassed.class).getId(),
                                                    ctx.getRequest().getBodyStream(ctx.getRequest().getContentLength()))
                                            ))
                                    )
                                    //apps
                                    .path("apps", ctx ->
                                            ctx.byMethod(m -> m
                                                    .get(() -> doPaginated(appResource::listApps).handle(ctx))
                                                    .put(() -> assureUser(ctx1 -> ctx.render(
                                                            merge(ctx, App.newBuilder(), Arrays.asList(
                                                                    App.ID_FIELD_NUMBER,
                                                                    App.ACTIVE_FIELD_NUMBER,
                                                                    App.TAGS_FIELD_NUMBER,
                                                                    App.DEVELOPER_FIELD_NUMBER,
                                                                    App.TAGS_FIELD_NUMBER)
                                                            ).map(app ->
                                                                    appResource.createApp(
                                                                            ctx.get(JWTokenPassed.class).getId(),
                                                                            app.build()
                                                                    )
                                                            )
                                                    )).handle(ctx)
                                                    )
                                            )
                                    )
                                    .path("apps/:id", ctx ->
                                            ctx.byMethod(m -> m
                                                    .get(() -> {
                                                        int id = assertParameterInt(ctx, "id");
                                                        List<String> platforms = ctx.getRequest().getQueryParams().asMultimap().get("platform");
                                                        ctx.render(appResource.getApp(id, platforms));
                                                    })
                                                    .patch(() -> assureUser(ctx1 -> ctx.render(
                                                            merge(ctx, App.newBuilder(), Arrays.asList(
                                                                    App.ACTIVE_FIELD_NUMBER,
                                                                    App.TAGS_FIELD_NUMBER,
                                                                    App.DEVELOPER_FIELD_NUMBER,
                                                                    App.AppVersion.DOWNLOAD_LINK_FIELD_NUMBER)
                                                            ).map(app -> appResource.updateApp(
                                                                            ctx.get(JWTokenPassed.class).getId(),
                                                                            assertParameterInt(ctx, "id"),
                                                                            app.build())
                                                            )
                                                    )).handle(ctx))
                                            )
                                    )
                                    .get("apps/search/:keyword", doPaginated((from, next, ctx) -> {
                                        String keyword = assertParameter(ctx, "keyword");
                                        return appResource.searchApps(from, next, keyword);
                                    }))
                                    .get("apps/:id/:major/:minor/:patch", ctx -> {
                                        List<String> platforms = ctx.getRequest().getQueryParams().asMultimap().get("platform");
                                        ctx.render(appResource.getAppVersion(
                                                assertParameterInt(ctx, "id"),
                                                assertParameterInt(ctx, "major"),
                                                assertParameterInt(ctx, "minor"),
                                                assertParameterInt(ctx, "patch"),
                                                platforms
                                        ));
                                    })
                                    .put("apps/:id/:major/:minor/:patch/:platform", assureUser(ctx -> {
                                        ctx.render(
                                                appResource.putInstance(
                                                        ctx.get(JWTokenPassed.class).getId(),
                                                        assertParameterInt(ctx, "id"),
                                                        assertParameterInt(ctx, "major"),
                                                        assertParameterInt(ctx, "minor"),
                                                        assertParameterInt(ctx, "patch"),
                                                        assertParameter(ctx, "platform"),
                                                        ctx.getRequest().getBodyStream(ctx.getRequest().getContentLength()))
                                        );
                                    }))
                                    .put("apps/:id/picture", assureUser(ctx -> {
                                        ctx.render(
                                                appResource.putPicture(
                                                        ctx.get(JWTokenPassed.class).getId(),
                                                        assertParameterInt(ctx, "id"),
                                                        ctx.getRequest().getBodyStream())
                                        );
                                    }));
                            if (fileDir != null) {
                                put.prefix("data", f -> f.files());
                            }
                        }
                )
        );
    }

    /**
     * merges the requests body with the Builder
     *
     * @param context  the Context of the Request with the JSON Representation of the Message as a Body
     * @param t        the builder
     * @param excluded the field numbers of the optional fields
     * @param <T>      the type of the builder
     * @return the builder with everything set
     * @throws BadRequestException if an error occurred while parsing the JSON or wrong content-type
     */
    private <T extends Message.Builder> Promise<T> merge(Context context, T t, List<Integer> excluded) throws BadRequestException {
        assertJson(context);
        return context.getRequest().getBody()
                .map(TypedData::getText)
                .map(body -> {
                    logger.trace("received body: {}", body);
                    try {
                        parser.merge(body, t);
                    } catch (InvalidProtocolBufferException e) {
                        throw new BadRequestException("error while parsing JSON", e);
                    }
                    t.getDescriptorForType().getFields().stream()
                            .filter(field -> !field.isRepeated() && !t.hasField(field))
                            .filter(field -> !excluded.contains(field.getNumber()))
                            .findAny()
                            .ifPresent(field -> {
                                throw new BadRequestException("field must be set:" + field.getFullName());
                            });
                    return t;
                });
    }

    /**
     * executes the consumer if the client is authorized
     *
     * @param contextConsumer the consumer to execute
     * @return a handler that executes the consumer if authorized
     */
    private Handler doAuthorized(Consumer<Context> contextConsumer) {
        return ctx -> {
            ctx.maybeGet(JWTokenPassed.class)
                    .orElseThrow(() -> new UnauthorizedException("Client needs the Authorization-header to access the method"));
            contextConsumer.accept(ctx);
        };
    }

    /**
     * executes the consumer if the client is an izou instance
     *
     * @param contextConsumer the consumer to execute
     * @return a handler that executes the consumer if client is an izou instance
     */
    private Handler assureIzou(Handler contextConsumer) {
        return ctx -> {
            ctx.maybeGet(JWTokenPassed.class)
                    .orElseThrow(() -> new UnauthorizedException("Client needs the Authorization-header to access the method"));
            if (!ctx.get(JWTokenPassed.class).getSubject().equals(Subject.IZOU)) {
                throw new UnauthorizedException("Client needs to be an izou-instance");
            } else if (ctx.get(JWTokenPassed.class).isRefresh()) {
                throw new UnauthorizedException("Token is refresh token");
            }
            contextConsumer.handle(ctx);
        };
    }

    /**
     * executes the consumer if the client is an izou instance
     *
     * @param contextConsumer the consumer to execute
     * @return a handler that executes the consumer if client is an izou instance
     */
    private Handler assureUser(Handler contextConsumer) {
        return ctx -> {
            ctx.maybeGet(JWTokenPassed.class)
                    .orElseThrow(() -> new UnauthorizedException("Client needs the Authorization-header to access the method"));
            if (!ctx.get(JWTokenPassed.class).getSubject().equals(Subject.USER)) {
                throw new UnauthorizedException("Client needs to be an user");
            } else if (ctx.get(JWTokenPassed.class).isRefresh()) {
                throw new UnauthorizedException("Token is refresh token");
            }
            contextConsumer.handle(ctx);
        };
    }
}
