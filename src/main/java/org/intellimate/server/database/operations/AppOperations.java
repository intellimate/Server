package org.intellimate.server.database.operations;

import org.intellimate.server.BadRequestException;
import org.intellimate.server.database.model.tables.records.AppDependencyRecord;
import org.intellimate.server.database.model.tables.records.AppInstanceRecord;
import org.intellimate.server.database.model.tables.records.AppRecord;
import org.intellimate.server.database.model.tables.records.AppVersionRecord;
import org.intellimate.server.proto.App;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.lambda.tuple.Tuple2;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.intellimate.server.database.model.Tables.*;

/**
 * @author LeanderK
 * @version 1.0
 */
//TODO dependencies
public class AppOperations extends AbstractOperations {
    private final String JAVA_PLATFORM = "java";

    public AppOperations(DSLContext create) {
        super(create);
    }

    public Optional<App.AppVersion> getAppVersion(int appID, int major, int minor, int patch, Set<String> platforms) {
        Set<String> queryPlatforms = new HashSet<>(platforms);
        queryPlatforms.add(JAVA_PLATFORM);
        Result<Record> app = create.select(APP_VERSION.fields())
                .select(APP_INSTANCE.PLATFORM, APP_INSTANCE.ID_APP_INSTANCE, APP_INSTANCE.ACTIVE, APP_INSTANCE.ERROR, APP_INSTANCE.WARNING)
                .select(APP_DEPENDENCY.DEPENDENCY)
                .from(APP_VERSION)
                .join(APP_INSTANCE).on(
                        APP_VERSION.APP.eq(APP_INSTANCE.APP_REFERENCE)
                                .and(APP_INSTANCE.PLATFORM.in(queryPlatforms))
                                .and(APP_INSTANCE.ACTIVE.eq(true))
                )
                .leftJoin(APP_DEPENDENCY).on(APP_DEPENDENCY.SUBJECT.eq(APP_INSTANCE.ID_APP_INSTANCE))
                .where(APP_VERSION.APP.eq(appID))
                .and(APP_VERSION.MAJOR.eq(major))
                .and(APP_VERSION.MINOR.eq(minor))
                .and(APP_VERSION.PATCH.eq(patch))
                .fetch();

        if (app.isEmpty()) {
            return Optional.empty();
        }

        Set<String> resultingPlatforms = new HashSet<>(app.getValues(APP_INSTANCE.PLATFORM));
        if (resultingPlatforms.isEmpty()) {
            return Optional.empty();
        }

        String platform = resultingPlatforms.stream()
                .filter(possiblePlatform -> !possiblePlatform.equals(JAVA_PLATFORM))
                .filter(platforms::contains)
                .findAny()
                .orElse(JAVA_PLATFORM);

        Record instance = app.stream()
                .filter(record -> platform.equals(record.getValue(APP_INSTANCE.PLATFORM)))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Something went terribly wrong"));


        Set<Integer> unresolvedDependencies = new HashSet<>(app.getValues(APP_DEPENDENCY.DEPENDENCY));
        List<App> dependencies = new ArrayList<>();
        Set<Integer> resolved = new HashSet<>();

        while (!unresolvedDependencies.isEmpty()) {
            Integer dependency = unresolvedDependencies.iterator().next();
            unresolvedDependencies.remove(dependency);
            resolved.add(dependency);
            App resolvedDependency = getDependency(dependency, platforms);
            List<Integer> newDependencies = resolvedDependency.getVersionsList().stream()
                    .flatMap(version -> version.getDependenciesList().stream())
                    .map(App::getId)
                    .filter(id -> !resolved.contains(id))
                    .collect(Collectors.toList());
            unresolvedDependencies.addAll(newDependencies);
            dependencies.add(resolvedDependency);
        }

        return Optional.of(App.AppVersion.newBuilder()
                .setVersion(String.format("%d.%d.%d", major, minor, patch))
                .setPlatform(platform)
                .setDownloadLink(""+instance.getValue(APP_INSTANCE.ID_APP_INSTANCE))
                .setActive(instance.getValue(APP_INSTANCE.ACTIVE))
                .setError(instance.getValue(APP_INSTANCE.ERROR))
                .addAllWarnings(
                        Arrays.stream(instance.getValue(APP_INSTANCE.WARNING).split(";"))
                            .filter(warning -> !warning.isEmpty())
                            .collect(Collectors.toList())
                )
                .addAllDependencies(dependencies)
                .build());
    }

    private App getDependency(int appID, Set<String> platforms) {
        Function<String, List<App>> resolveDependencies = concatenated ->
                Arrays.stream(concatenated.split(","))
                .map(Integer::parseInt)
                .map(id -> App.newBuilder().setId(id).build())
                .collect(Collectors.toList());

        Field<String> dependencies = DSL.groupConcat(APP_DEPENDENCY.DEPENDENCY, ",").as("dependencies");
        return create.select(APP.ID_APP)
                .select(APP.NAME)
                .select(dependencies)
                .select(APP_VERSION.MAJOR, APP_VERSION.MINOR, APP_VERSION.PATCH)
                .from(APP)
                .innerJoin(APP_VERSION).onKey()
                .innerJoin(APP_INSTANCE).on(
                        APP_INSTANCE.APP_REFERENCE.eq(APP_VERSION.ID_APP_VERSION)
                                .and(APP_INSTANCE.PLATFORM.in(platforms))
                                .and(APP_INSTANCE.ACTIVE.eq(true))
                )
                .leftJoin(APP_DEPENDENCY).on(
                        APP_DEPENDENCY.SUBJECT.eq(APP_INSTANCE.ID_APP_INSTANCE)
                )
                .where(APP.ACTIVE.eq(true))
                .groupBy(APP.ID_APP, APP.NAME, APP_VERSION.MAJOR, APP_VERSION.MINOR, APP_VERSION.PATCH)
                .orderBy(APP_VERSION.MAJOR.desc(), APP_VERSION.MINOR.desc(), APP_VERSION.PATCH.desc())
                .limit(1)
                .fetchOptional()
                .map(record ->
                        App.newBuilder()
                                .setId(record.getValue(APP.ID_APP))
                                .setName(record.getValue(APP.NAME))
                                .addVersions(App.AppVersion.newBuilder()
                                        .setVersion(String.format("%d.%d.%d", record.getValue(APP_VERSION.MAJOR),
                                                record.getValue(APP_VERSION.MINOR),
                                                record.getValue(APP_VERSION.PATCH)))
                                        .addAllDependencies(resolveDependencies.apply(record.getValue(dependencies)))
                                        .build()
                                )
                                .build()
                )
                .orElseGet(() ->
                        getApp(appID)
                                .map(tuple ->
                                        App.newBuilder()
                                                .setId(appID)
                                                .setName(tuple.v1.getName())
                                                .build()
                                )
                                .orElseGet(() -> App.newBuilder().setId(appID).build())
                );
    }

    /**
     * returns the Version for the app
     * @param appID the primary key of the app
     * @param platforms the platforms
     * @return the list of appVersions
     */
    public List<Record> getVersions(int appID, List<String> platforms) {
        Set<String> queryPlatforms = new HashSet<>(platforms);
        queryPlatforms.add(JAVA_PLATFORM);
        return create.select(APP_VERSION.fields())
                .select(APP_INSTANCE.ID_APP_INSTANCE)
                .select(APP_INSTANCE.PLATFORM)
                .select(APP_INSTANCE.ERROR)
                .select(APP_INSTANCE.WARNING)
                .select(APP_INSTANCE.ACTIVE)
                .from(APP_VERSION)
                .innerJoin(APP_INSTANCE).on(
                        APP_VERSION.ID_APP_VERSION.eq(APP_INSTANCE.APP_REFERENCE)
                        .and(APP_INSTANCE.PLATFORM.in(queryPlatforms))
                )
                .where(APP_VERSION.APP.eq(appID))
                .fetch();
    }

    /**
     * returns the app for the passed id
     * @param appID the appID
     * @return the app and the tags, or empty if not found
     */
    public Optional<Tuple2<AppRecord, List<String>>> getApp(int appID) {
        Field<String> app_tag_name = APP_TAG.NAME.as("app_tag_name");
        Map<AppRecord, List<String>> appMap = create.select(APP.fields())
                .select(app_tag_name)
                .from(APP)
                .leftJoin(APP_ACTIVE_TAG).onKey()
                .leftJoin(APP_TAG).on(APP_ACTIVE_TAG.TAG.eq(APP_TAG.ID_APP_TAGS))
                .where(APP.ID_APP.eq(appID))
                .fetchGroups(record -> record.into(APP), record -> record.getValue(app_tag_name));
        if (appMap.isEmpty()) {
            return Optional.empty();
        } else {
            Map.Entry<AppRecord, List<String>> entry = appMap.entrySet().iterator().next();
            return Optional.of(new Tuple2<>(entry.getKey(), entry.getValue()));
        }
    }

    public Range<App, Integer> listApps(int cursor, boolean next, int limit) {
        SelectConditionStep<AppRecord> query = create.selectFrom(APP)
                .where(APP.ACTIVE.eq(true));
        return getNextRange(query, APP.ID_APP, APP, cursor, next, limit)
                .map(appRecord -> App.newBuilder()
                        .setId(appRecord.getIdApp())
                        .setName(appRecord.getName())
                        .setActive(appRecord.getActive())
                        .build()
                );
    }

    public Range<App, Integer> searchApps(int cursor, boolean next, int limit, String search) {
        SelectConditionStep<Record> searchQuery = create.selectDistinct(APP.fields())
                .from(APP)
                .join(USER).onKey()
                .join(APP_ACTIVE_TAG).onKey()
                .join(APP_TAG).on(
                        APP_ACTIVE_TAG.TAG.eq(APP_TAG.ID_APP_TAGS)
                )
                .where(
                        APP.NAME.contains(search)
                                .or(APP.DESCRIPTION.contains(search))
                                .or(USER.NAME.contains(search))
                                .or(APP_TAG.NAME.contains(search))
                                .and(APP.ACTIVE.eq(true))
                );
        return getNextRange(searchQuery, APP.ID_APP, APP, cursor, next, limit)
                .map(record -> record.into(APP))
                .map(appRecord -> App.newBuilder()
                        .setId(appRecord.getIdApp())
                        .setName(appRecord.getName())
                        .setActive(appRecord.getActive())
                        .build()
                );
    }

    public Range<App, Integer> listUsersApps(int cursor, boolean next, int limit, int userID) {
        SelectConditionStep<AppRecord> query = create.selectFrom(APP)
                .where(APP.DEVELOPER.eq(userID));
        return getNextRange(query, APP.ID_APP, APP, cursor, next, limit)
                .map(appRecord -> App.newBuilder()
                        .setId(appRecord.getIdApp())
                        .setName(appRecord.getName())
                        .setActive(appRecord.getActive())
                        .build()
                );
    }

    //TODO: same version mutiple times, version max 3 char long
    public App insertApp(int user, App app) {
        AppRecord appRecord = new AppRecord(null, user, app.getName(), app.getDescription(), false, app.getPackage());
        AppRecord insertedApp = create.insertInto(APP)
                .set(appRecord)
                .returning()
                .fetchOne();
        Timestamp timestamp = Timestamp.from(Instant.now());
        List<App.AppVersion> versions = app.getVersionsList().stream()
                .map(appVersion -> {
                    String[] split = appVersion.getVersion().split("\\.");
                    if (split.length == 3) {
                        List<Integer> version = Arrays.stream(split).map(Integer::parseInt).collect(Collectors.toList());
                        if (version.stream().anyMatch(number -> number > 999)) {
                            throw new BadRequestException("Illegal Version, max is 999.999.999");
                        }
                        AppVersionRecord appVersionRecord = new AppVersionRecord(null,
                                insertedApp.getIdApp(), timestamp, version.get(0), version.get(1), version.get(2));
                        AppVersionRecord insertedVersion = create.insertInto(APP_VERSION)
                                .set(appVersionRecord)
                                .returning()
                                .fetchOne();
                        String platform = appVersion.getPlatform();
                        if (platform == null || platform.isEmpty()) {
                            platform = "java";
                        }
                        AppInstanceRecord insertedInstance = create.insertInto(APP_INSTANCE)
                                .set(new AppInstanceRecord(null, insertedVersion.getIdAppVersion(), platform, false, "", ""))
                                .returning()
                                .fetchOne();
                        List<AppDependencyRecord> dependencies = appVersion.getDependenciesList().stream()
                                .map(App::getId)
                                .map((Function<Integer, AppDependencyRecord>) id -> new AppDependencyRecord(null, insertedInstance.getIdAppInstance(), id))
                                .collect(Collectors.toList());
                        create.batchInsert(dependencies);
                        return appVersion;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return App.newBuilder()
                .setId(insertedApp.getIdApp())
                .setName(app.getName())
                .setActive(false)
                .setDeveloper(app.getDeveloper())
                .setDescription(app.getDescription())
                .addAllVersions(versions)
                .build();
    }

    //TODO: same version mutiple times, version max 3 char long
    public Tuple2<App, List<AppInstanceRecord>> updateApp(int user, App app) {
        AppRecord appRecord = new AppRecord();
        appRecord.setIdApp(app.getId());
        appRecord.setName(app.getName());
        appRecord.setDescription(app.getDescription());
        int affected = create.update(APP)
                .set(appRecord)
                .where(APP.ID_APP.eq(app.getId()))
                .execute();
        if (affected != 1) {
            throw new BadRequestException(String.format("unable to update app %d", app.getId()));
        }
        AppRecord updated = create.selectFrom(APP)
                .where(APP.ID_APP.eq(app.getId()))
                .fetchOne();
        Timestamp timestamp = Timestamp.from(Instant.now());
        Tuple2<Result<AppInstanceRecord>, List<App.AppVersion>> result = create.transactionResult(conf -> {
            Map<String, AppVersionRecord> existingVersion = create.selectFrom(APP_VERSION)
                    .where(APP_VERSION.APP.eq(updated.getIdApp()))
                    .fetchMap((RecordMapper<? super AppVersionRecord, String>) record -> String.format("%d.%d.%d", record.getMajor(), record.getMinor(), record.getPatch()));
            Set<String> versions = app.getVersionsList().stream()
                    .map(App.AppVersion::getVersion)
                    .collect(Collectors.toSet());
            List<Integer> toDelete = existingVersion.keySet().stream()
                    .filter(version -> !versions.contains(version))
                    .map(existingVersion::get)
                    .map(AppVersionRecord::getIdAppVersion)
                    .collect(Collectors.toList());

            List<App.AppVersion> toInsert = app.getVersionsList().stream()
                    .filter(appVersion -> !existingVersion.containsKey(appVersion.getVersion()))
                    .collect(Collectors.toList());

            Result<AppInstanceRecord> toDeleteFromData = DSL.using(conf).selectFrom(APP_INSTANCE)
                    .where(APP_INSTANCE.APP_REFERENCE.in(toDelete))
                    .fetch();
            DSL.using(conf)
                    .deleteFrom(APP_VERSION)
                    .where(APP_VERSION.ID_APP_VERSION.in(toDelete))
                    .execute();
            return new Tuple2<>(toDeleteFromData, toInsert);
        });
        List<AppInstanceRecord> toDeleteFromData = result.v1;
        List<App.AppVersion> toInsert = result.v2;
        List<App.AppVersion> versions = toInsert.stream()
                .map(appVersion -> {
                    String[] split = appVersion.getVersion().split("\\.");
                    if (split.length == 3) {
                        List<Integer> version = Arrays.stream(split).map(Integer::parseInt).collect(Collectors.toList());
                        if (version.stream().anyMatch(number -> number > 999)) {
                            throw new BadRequestException("Illegal Version, max is 999.999.999");
                        }
                        AppVersionRecord appVersionRecord = new AppVersionRecord(null,
                                updated.getIdApp(), timestamp, version.get(0), version.get(1), version.get(2));
                        AppVersionRecord insertedVersion = create.insertInto(APP_VERSION)
                                .set(appVersionRecord)
                                .returning()
                                .fetchOne();
                        String platform = appVersion.getPlatform();
                        if (platform == null || platform.isEmpty()) {
                            platform = "java";
                        }
                        AppInstanceRecord insertedInstance = create.insertInto(APP_INSTANCE)
                                .set(new AppInstanceRecord(null, insertedVersion.getIdAppVersion(), platform, false, "", ""))
                                .returning()
                                .fetchOne();
                        List<AppDependencyRecord> dependencies = appVersion.getDependenciesList().stream()
                                .map(App::getId)
                                .map((Function<Integer, AppDependencyRecord>) id -> new AppDependencyRecord(null, insertedInstance.getIdAppInstance(), id))
                                .collect(Collectors.toList());
                        create.batchInsert(dependencies);
                        return appVersion;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new Tuple2<>(App.newBuilder()
                .setId(updated.getIdApp())
                .setName(app.getName())
                .setActive(false)
                .setDeveloper(app.getDeveloper())
                .setDescription(app.getDescription())
                .addAllVersions(versions)
                .build(), toDeleteFromData);
    }

    public Optional<AppInstanceRecord> getAppInstance(int app, int major, int minor, int patch, String platform) {
        return create.select(APP_INSTANCE.fields())
                .from(APP_VERSION)
                .innerJoin(APP_INSTANCE).on(
                        APP_VERSION.ID_APP_VERSION.eq(APP_INSTANCE.APP_REFERENCE)
                                .and(APP_INSTANCE.PLATFORM.eq(platform))
                )
                .where(
                        APP_VERSION.APP.eq(app)
                                .and(APP_VERSION.MAJOR.eq(major))
                                .and(APP_VERSION.MINOR.eq(minor))
                                .and(APP_VERSION.PATCH.eq(patch))
                )
                .fetchOptional()
                .map(record -> record.into(APP_INSTANCE));
    }

    public void setActive(int app, int instanceID) {
        create.update(APP_INSTANCE)
                .set(APP_INSTANCE.ACTIVE, true)
                .where(APP_INSTANCE.ID_APP_INSTANCE.eq(instanceID))
                .execute();

        create.update(APP)
                .set(APP.ACTIVE, true)
                .where(APP.ID_APP.eq(app))
                .execute();
    }

    public List<AppRecord> getUsersApps(int user) {
        return create.selectFrom(APP)
                .where(APP.DEVELOPER.eq(user))
                .fetch();
    }

    public void addWarning(String warning, int appInstance) {
        //TODO warning after jooq update
        create.update(APP_INSTANCE)
                .set(APP_INSTANCE.ERROR, DSL.concat(APP_INSTANCE.WARNING, ";"+warning))
                .where(APP_INSTANCE.ID_APP_INSTANCE.eq(appInstance))
                .execute();
    }

    public void setError(String error, int appInstance) {
        create.update(APP_INSTANCE)
                .set(APP_INSTANCE.ERROR, error)
                .where(APP_INSTANCE.ID_APP_INSTANCE.eq(appInstance))
                .execute();
    }

    public Map<String, Integer> getAppsForPackages(Set<String> packageNames) {
        return create.select(APP.ID_APP, APP.PACKAGE)
                .from(APP)
                .where(APP.PACKAGE.in(packageNames))
                .fetchMap(APP.PACKAGE, Record2::value1);
    }

    public void setDependencies(List<AppDependencyRecord> dependencies, int appInstance) {
        create.transaction(conf -> {
            DSL.using(conf).deleteFrom(APP_DEPENDENCY)
                    .where(APP_DEPENDENCY.SUBJECT.eq(appInstance))
                    .execute();

            DSL.using(conf).batchInsert(dependencies).execute();
        });
    }

    public Optional<AppRecord> getAppRecord(int appId) {
        return create.selectFrom(APP)
                .where(APP.ID_APP.eq(appId))
                .fetchOptional();
    }
}
