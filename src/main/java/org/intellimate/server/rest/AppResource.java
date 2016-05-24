package org.intellimate.server.rest;

import io.netty.buffer.ByteBuf;
import org.intellimate.server.BadRequestException;
import org.intellimate.server.NotFoundException;
import org.intellimate.server.data.FileStorage;
import org.intellimate.server.database.model.Tables;
import org.intellimate.server.database.model.tables.records.AppInstanceRecord;
import org.intellimate.server.database.model.tables.records.AppRecord;
import org.intellimate.server.database.model.tables.records.AppVersionRecord;
import org.intellimate.server.database.model.tables.records.UserRecord;
import org.intellimate.server.database.operations.AppOperations;
import org.intellimate.server.database.operations.UserOperations;
import org.intellimate.server.proto.App;
import org.intellimate.server.proto.AppList;
import org.jooq.lambda.tuple.Tuple2;
import ratpack.exec.Promise;
import ratpack.stream.TransformablePublisher;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * this class is concerned with all the app-related requests
 * @author LeanderK
 * @version 1.0
 */
public class AppResource {
    private final AppOperations appOperations;
    private final FileStorage fileStorage;
    private final UserOperations userOperations;

    public AppResource(AppOperations appOperations, FileStorage fileStorage, UserOperations userOperations) {
        this.appOperations = appOperations;
        this.fileStorage = fileStorage;
        this.userOperations = userOperations;
    }

    public App getApp(int appID, List<String> platforms) {
        return appOperations.getApp(appID)
                .map(data -> {
                    List<App.AppVersion> versions = appOperations.getVersions(appID, platforms).stream()
                            .map(record -> {
                                AppVersionRecord versionRecord = record.into(Tables.APP_VERSION);
                                return App.AppVersion.newBuilder()
                                        .setVersion(String.format("%d.%d.%d",
                                                versionRecord.getMajor(),
                                                versionRecord.getMinor(),
                                                versionRecord.getPatch()))
                                        .setPlatform(record.get(Tables.APP_INSTANCE.PLATFORM))
                                        .setDownloadLink(fileStorage.getLinkForExactName(String.format("appinstance%d.zip",
                                                record.getValue(Tables.APP_INSTANCE.ID_APP_INSTANCE))))
                                        .build();
                            })
                            .collect(Collectors.toList());
                    AppRecord app = data.v1;
                    List<String> tags = data.v2;
                    tags.removeIf(tag -> tag == null);
                    return App.newBuilder()
                            .setId(app.getIdApp())
                            .setName(app.getName())
                            .setDescription(app.getDescription())
                            .setDeveloper(
                                    userOperations.getUser(app.getDeveloper())
                                            .map(UserRecord::getName)
                                            .orElse("unknown")
                            )
                            .addAllTags(tags)
                            .addAllVersions(versions)
                            .build();
                })
                .orElseThrow(() -> new NotFoundException(String.format("App %d not found", appID)));
    }

    public App.AppVersion getAppVersion(int appID, int major, int minor, int patch, List<String> platforms) {
        return appOperations.getAppVersion(appID, major, minor, patch, platforms)
                .map(appVersion -> {
                    return appVersion
                            .toBuilder()
                            .setDownloadLink(fileStorage.getLinkForExactName(String.format("appinstance%s.zip", appVersion.getDownloadLink())))
                            .build();
                })
                .orElseThrow(() -> new NotFoundException(String.format("App-Version %d.%d.%d for app %d not found",
                        major, minor, patch, appID)));
    }

    public Paginated<Integer> listApps(int from, boolean asc) {
        return appOperations.listApps(from, asc, 20)
                .constructPaginated(AppList.newBuilder(), AppList.Builder::addAllApps);
    }

    public Paginated<Integer> searchApps(int from, boolean asc, String search) {
        return appOperations.searchApps(from, asc, 20, search)
                .constructPaginated(AppList.newBuilder(), AppList.Builder::addAllApps);
    }

    public App createApp(int userID, App app) {
        UserRecord user = userOperations.getUser(userID)
                .orElseThrow(() -> new BadRequestException(String.format("invalid user %d", userID)));
        if (app.getDescription().isEmpty()) {
            throw new BadRequestException("description must not be empty");
        }

        if (app.getName().isEmpty()) {
            throw new BadRequestException("name must not be empty");
        }

        return appOperations.insertApp(userID, app);
    }

    public App updateApp(int userID, int appID, App app) {
        UserRecord user = userOperations.getUser(userID)
                .orElseThrow(() -> new BadRequestException(String.format("invalid user %d", userID)));

        AppRecord appRecord = appOperations.getApp(appID)
                .orElseThrow(() -> new NotFoundException(String.format("App %d is not existing", appID)))
                .v1;

        if (app.getDescription().isEmpty()) {
            throw new BadRequestException("description must not be empty");
        }

        if (app.getName().isEmpty()) {
            throw new BadRequestException("name must not be empty");
        }

        if (!appRecord.getDeveloper().equals(userID)) {
            throw new BadRequestException("can not change the app of other developers");
        }

        App toUpdate = app.toBuilder().setId(appID).setActive(appRecord.getActive()).build();

        Tuple2<App, List<AppInstanceRecord>> result = appOperations.updateApp(userID, toUpdate);

        List<AppInstanceRecord> toDelete = result.v2;

        toDelete.forEach(instance -> {
            fileStorage.delete(String.format("appinstance%d", instance.getIdAppInstance()));
        });

        return result.v1;
    }

    public Promise<String> putInstance(int userID, int app, int major, int minor, int patch, String platform, TransformablePublisher<? extends ByteBuf> input) {
        UserRecord user = userOperations.getUser(userID)
                .orElseThrow(() -> new BadRequestException(String.format("invalid user %d", userID)));
        Integer developer = appOperations.getApp(app)
                .orElseThrow(() -> new BadRequestException(String.format("invalid app %d", app)))
                .v1.getDeveloper();
        if (developer != userID) {
            throw new BadRequestException(String.format("user %d is not developer for app %d", userID, app));
        }
        AppInstanceRecord appInstanceRecord = appOperations.getAppInstance(app, major, minor, patch, platform)
                .orElseThrow(() -> new BadRequestException("instance is not existing"));
        String name = String.format("appinstance%d.zip", appInstanceRecord.getIdAppInstance());
        CompletableFuture<Long> future = fileStorage.saveExact(input, name);

        return Promise.async(down -> down.accept(future))
                .map(ignore -> {
                    appOperations.setActive(app, appInstanceRecord.getIdAppInstance());
                    return fileStorage.getLinkForExactName(name);
                });
    }

    public Promise<String> putPicture(int userID, int app, TransformablePublisher<? extends ByteBuf> input) {
        UserRecord user = userOperations.getUser(userID)
                .orElseThrow(() -> new BadRequestException(String.format("invalid user %d", userID)));
        Integer developer = appOperations.getApp(app)
                .orElseThrow(() -> new BadRequestException(String.format("invalid app %d", app)))
                .v1.getDeveloper();
        if (developer != userID) {
            throw new BadRequestException(String.format("user %d is not developer for app %d", userID, app));
        }
        String name = "picture" + app + ".jpg";
        CompletableFuture<Long> future = fileStorage.saveExact(input, name);
        return Promise.of(down -> down.accept(future))
                .map(ignore -> fileStorage.getLinkForExactName(name));
    }
}
