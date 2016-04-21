package org.intellimate.server.rest;

import org.intellimate.server.BadRequestException;
import org.intellimate.server.NotFoundException;
import org.intellimate.server.data.FileStorage;
import org.intellimate.server.database.model.Tables;
import org.intellimate.server.database.model.tables.records.AppRecord;
import org.intellimate.server.database.model.tables.records.AppVersionRecord;
import org.intellimate.server.database.model.tables.records.UserRecord;
import org.intellimate.server.database.operations.AppOperations;
import org.intellimate.server.database.operations.UserOperations;
import org.intellimate.server.proto.App;
import org.intellimate.server.proto.AppList;
import org.jooq.Record;

import java.util.List;
import java.util.function.Function;
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
                            .map((Function<Record, App.AppVersion>) record -> {
                                AppVersionRecord versionRecord = record.into(Tables.APP_VERSION);
                                return App.AppVersion.newBuilder()
                                        .setVersion(String.format("%d.%d.%d",
                                                versionRecord.getMajor(),
                                                versionRecord.getMinor(),
                                                versionRecord.getPatch()))
                                        .setDownloadLink(String.format("appinstance%d",
                                                record.getValue(Tables.APP_INSTANCE.ID_APP_INSTANCE)))
                                        .build();
                            })
                            .collect(Collectors.toList());
                    AppRecord app = data.v1;
                    List<String> tags = data.v2;
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

        if (app.getName().isEmpty()) {
            throw new BadRequestException("name must not be empty");
        }

        return null;
    }
}
