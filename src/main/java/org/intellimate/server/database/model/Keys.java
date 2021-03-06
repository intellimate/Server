/**
 * This class is generated by jOOQ
 */
package org.intellimate.server.database.model;


import javax.annotation.Generated;

import org.intellimate.server.database.model.tables.App;
import org.intellimate.server.database.model.tables.AppActiveTag;
import org.intellimate.server.database.model.tables.AppDependency;
import org.intellimate.server.database.model.tables.AppInstance;
import org.intellimate.server.database.model.tables.AppTag;
import org.intellimate.server.database.model.tables.AppVersion;
import org.intellimate.server.database.model.tables.DatabaseVersion;
import org.intellimate.server.database.model.tables.Izou;
import org.intellimate.server.database.model.tables.IzouInstance;
import org.intellimate.server.database.model.tables.User;
import org.intellimate.server.database.model.tables.records.AppActiveTagRecord;
import org.intellimate.server.database.model.tables.records.AppDependencyRecord;
import org.intellimate.server.database.model.tables.records.AppInstanceRecord;
import org.intellimate.server.database.model.tables.records.AppRecord;
import org.intellimate.server.database.model.tables.records.AppTagRecord;
import org.intellimate.server.database.model.tables.records.AppVersionRecord;
import org.intellimate.server.database.model.tables.records.DatabaseVersionRecord;
import org.intellimate.server.database.model.tables.records.IzouInstanceRecord;
import org.intellimate.server.database.model.tables.records.IzouRecord;
import org.intellimate.server.database.model.tables.records.UserRecord;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;


/**
 * A class modelling foreign key relationships between tables of the <code>izoudb</code> 
 * schema
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<AppRecord, Integer> IDENTITY_APP = Identities0.IDENTITY_APP;
    public static final Identity<AppActiveTagRecord, Integer> IDENTITY_APP_ACTIVE_TAG = Identities0.IDENTITY_APP_ACTIVE_TAG;
    public static final Identity<AppDependencyRecord, Integer> IDENTITY_APP_DEPENDENCY = Identities0.IDENTITY_APP_DEPENDENCY;
    public static final Identity<AppInstanceRecord, Integer> IDENTITY_APP_INSTANCE = Identities0.IDENTITY_APP_INSTANCE;
    public static final Identity<AppTagRecord, Integer> IDENTITY_APP_TAG = Identities0.IDENTITY_APP_TAG;
    public static final Identity<AppVersionRecord, Integer> IDENTITY_APP_VERSION = Identities0.IDENTITY_APP_VERSION;
    public static final Identity<DatabaseVersionRecord, Integer> IDENTITY_DATABASE_VERSION = Identities0.IDENTITY_DATABASE_VERSION;
    public static final Identity<IzouRecord, Integer> IDENTITY_IZOU = Identities0.IDENTITY_IZOU;
    public static final Identity<IzouInstanceRecord, Integer> IDENTITY_IZOU_INSTANCE = Identities0.IDENTITY_IZOU_INSTANCE;
    public static final Identity<UserRecord, Integer> IDENTITY_USER = Identities0.IDENTITY_USER;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<AppRecord> KEY_APP_PRIMARY = UniqueKeys0.KEY_APP_PRIMARY;
    public static final UniqueKey<AppRecord> KEY_APP_NAME_UNIQUE = UniqueKeys0.KEY_APP_NAME_UNIQUE;
    public static final UniqueKey<AppRecord> KEY_APP_PACKAGE_UNIQUE = UniqueKeys0.KEY_APP_PACKAGE_UNIQUE;
    public static final UniqueKey<AppActiveTagRecord> KEY_APP_ACTIVE_TAG_PRIMARY = UniqueKeys0.KEY_APP_ACTIVE_TAG_PRIMARY;
    public static final UniqueKey<AppDependencyRecord> KEY_APP_DEPENDENCY_PRIMARY = UniqueKeys0.KEY_APP_DEPENDENCY_PRIMARY;
    public static final UniqueKey<AppInstanceRecord> KEY_APP_INSTANCE_PRIMARY = UniqueKeys0.KEY_APP_INSTANCE_PRIMARY;
    public static final UniqueKey<AppTagRecord> KEY_APP_TAG_PRIMARY = UniqueKeys0.KEY_APP_TAG_PRIMARY;
    public static final UniqueKey<AppVersionRecord> KEY_APP_VERSION_PRIMARY = UniqueKeys0.KEY_APP_VERSION_PRIMARY;
    public static final UniqueKey<DatabaseVersionRecord> KEY_DATABASE_VERSION_PRIMARY = UniqueKeys0.KEY_DATABASE_VERSION_PRIMARY;
    public static final UniqueKey<IzouRecord> KEY_IZOU_PRIMARY = UniqueKeys0.KEY_IZOU_PRIMARY;
    public static final UniqueKey<IzouRecord> KEY_IZOU_VERSION_UNIQUE = UniqueKeys0.KEY_IZOU_VERSION_UNIQUE;
    public static final UniqueKey<IzouInstanceRecord> KEY_IZOU_INSTANCE_PRIMARY = UniqueKeys0.KEY_IZOU_INSTANCE_PRIMARY;
    public static final UniqueKey<UserRecord> KEY_USER_PRIMARY = UniqueKeys0.KEY_USER_PRIMARY;
    public static final UniqueKey<UserRecord> KEY_USER_EMAIL_UNIQUE = UniqueKeys0.KEY_USER_EMAIL_UNIQUE;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<AppActiveTagRecord, AppRecord> APP_ACTIVE_TAG_APP_REF = ForeignKeys0.APP_ACTIVE_TAG_APP_REF;
    public static final ForeignKey<AppDependencyRecord, AppInstanceRecord> APP_DEPENDENCY_SUBJECT = ForeignKeys0.APP_DEPENDENCY_SUBJECT;
    public static final ForeignKey<AppDependencyRecord, AppRecord> APP_DEPENDENCY_DEPENDENCY = ForeignKeys0.APP_DEPENDENCY_DEPENDENCY;
    public static final ForeignKey<AppInstanceRecord, AppVersionRecord> APP_INSTANCE_REF = ForeignKeys0.APP_INSTANCE_REF;
    public static final ForeignKey<AppVersionRecord, AppRecord> VERSIONED_APP = ForeignKeys0.VERSIONED_APP;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 extends AbstractKeys {
        public static Identity<AppRecord, Integer> IDENTITY_APP = createIdentity(App.APP, App.APP.ID_APP);
        public static Identity<AppActiveTagRecord, Integer> IDENTITY_APP_ACTIVE_TAG = createIdentity(AppActiveTag.APP_ACTIVE_TAG, AppActiveTag.APP_ACTIVE_TAG.ID_APP_ACTIVE_TAG);
        public static Identity<AppDependencyRecord, Integer> IDENTITY_APP_DEPENDENCY = createIdentity(AppDependency.APP_DEPENDENCY, AppDependency.APP_DEPENDENCY.ID_APP_DEPENDENCY);
        public static Identity<AppInstanceRecord, Integer> IDENTITY_APP_INSTANCE = createIdentity(AppInstance.APP_INSTANCE, AppInstance.APP_INSTANCE.ID_APP_INSTANCE);
        public static Identity<AppTagRecord, Integer> IDENTITY_APP_TAG = createIdentity(AppTag.APP_TAG, AppTag.APP_TAG.ID_APP_TAGS);
        public static Identity<AppVersionRecord, Integer> IDENTITY_APP_VERSION = createIdentity(AppVersion.APP_VERSION, AppVersion.APP_VERSION.ID_APP_VERSION);
        public static Identity<DatabaseVersionRecord, Integer> IDENTITY_DATABASE_VERSION = createIdentity(DatabaseVersion.DATABASE_VERSION, DatabaseVersion.DATABASE_VERSION.ID_DATABASE_VERSION);
        public static Identity<IzouRecord, Integer> IDENTITY_IZOU = createIdentity(Izou.IZOU, Izou.IZOU.ID_IZOU);
        public static Identity<IzouInstanceRecord, Integer> IDENTITY_IZOU_INSTANCE = createIdentity(IzouInstance.IZOU_INSTANCE, IzouInstance.IZOU_INSTANCE.ID_INSTANCES);
        public static Identity<UserRecord, Integer> IDENTITY_USER = createIdentity(User.USER, User.USER.ID_USER);
    }

    private static class UniqueKeys0 extends AbstractKeys {
        public static final UniqueKey<AppRecord> KEY_APP_PRIMARY = createUniqueKey(App.APP, "KEY_App_PRIMARY", App.APP.ID_APP);
        public static final UniqueKey<AppRecord> KEY_APP_NAME_UNIQUE = createUniqueKey(App.APP, "KEY_App_name_UNIQUE", App.APP.NAME);
        public static final UniqueKey<AppRecord> KEY_APP_PACKAGE_UNIQUE = createUniqueKey(App.APP, "KEY_App_package_UNIQUE", App.APP.PACKAGE);
        public static final UniqueKey<AppActiveTagRecord> KEY_APP_ACTIVE_TAG_PRIMARY = createUniqueKey(AppActiveTag.APP_ACTIVE_TAG, "KEY_App_Active_Tag_PRIMARY", AppActiveTag.APP_ACTIVE_TAG.ID_APP_ACTIVE_TAG);
        public static final UniqueKey<AppDependencyRecord> KEY_APP_DEPENDENCY_PRIMARY = createUniqueKey(AppDependency.APP_DEPENDENCY, "KEY_App_Dependency_PRIMARY", AppDependency.APP_DEPENDENCY.ID_APP_DEPENDENCY);
        public static final UniqueKey<AppInstanceRecord> KEY_APP_INSTANCE_PRIMARY = createUniqueKey(AppInstance.APP_INSTANCE, "KEY_App_Instance_PRIMARY", AppInstance.APP_INSTANCE.ID_APP_INSTANCE);
        public static final UniqueKey<AppTagRecord> KEY_APP_TAG_PRIMARY = createUniqueKey(AppTag.APP_TAG, "KEY_App_Tag_PRIMARY", AppTag.APP_TAG.ID_APP_TAGS);
        public static final UniqueKey<AppVersionRecord> KEY_APP_VERSION_PRIMARY = createUniqueKey(AppVersion.APP_VERSION, "KEY_App_Version_PRIMARY", AppVersion.APP_VERSION.ID_APP_VERSION);
        public static final UniqueKey<DatabaseVersionRecord> KEY_DATABASE_VERSION_PRIMARY = createUniqueKey(DatabaseVersion.DATABASE_VERSION, "KEY_Database_Version_PRIMARY", DatabaseVersion.DATABASE_VERSION.ID_DATABASE_VERSION);
        public static final UniqueKey<IzouRecord> KEY_IZOU_PRIMARY = createUniqueKey(Izou.IZOU, "KEY_Izou_PRIMARY", Izou.IZOU.ID_IZOU);
        public static final UniqueKey<IzouRecord> KEY_IZOU_VERSION_UNIQUE = createUniqueKey(Izou.IZOU, "KEY_Izou_version_UNIQUE", Izou.IZOU.VERSION);
        public static final UniqueKey<IzouInstanceRecord> KEY_IZOU_INSTANCE_PRIMARY = createUniqueKey(IzouInstance.IZOU_INSTANCE, "KEY_Izou_Instance_PRIMARY", IzouInstance.IZOU_INSTANCE.ID_INSTANCES);
        public static final UniqueKey<UserRecord> KEY_USER_PRIMARY = createUniqueKey(User.USER, "KEY_User_PRIMARY", User.USER.ID_USER);
        public static final UniqueKey<UserRecord> KEY_USER_EMAIL_UNIQUE = createUniqueKey(User.USER, "KEY_User_email_UNIQUE", User.USER.EMAIL);
    }

    private static class ForeignKeys0 extends AbstractKeys {
        public static final ForeignKey<AppActiveTagRecord, AppRecord> APP_ACTIVE_TAG_APP_REF = createForeignKey(org.intellimate.server.database.model.Keys.KEY_APP_PRIMARY, AppActiveTag.APP_ACTIVE_TAG, "app_active_tag_app_ref", AppActiveTag.APP_ACTIVE_TAG.APP);
        public static final ForeignKey<AppDependencyRecord, AppInstanceRecord> APP_DEPENDENCY_SUBJECT = createForeignKey(org.intellimate.server.database.model.Keys.KEY_APP_INSTANCE_PRIMARY, AppDependency.APP_DEPENDENCY, "app_dependency_subject", AppDependency.APP_DEPENDENCY.SUBJECT);
        public static final ForeignKey<AppDependencyRecord, AppRecord> APP_DEPENDENCY_DEPENDENCY = createForeignKey(org.intellimate.server.database.model.Keys.KEY_APP_PRIMARY, AppDependency.APP_DEPENDENCY, "app_dependency_dependency", AppDependency.APP_DEPENDENCY.DEPENDENCY);
        public static final ForeignKey<AppInstanceRecord, AppVersionRecord> APP_INSTANCE_REF = createForeignKey(org.intellimate.server.database.model.Keys.KEY_APP_VERSION_PRIMARY, AppInstance.APP_INSTANCE, "app_instance_ref", AppInstance.APP_INSTANCE.APP_REFERENCE);
        public static final ForeignKey<AppVersionRecord, AppRecord> VERSIONED_APP = createForeignKey(org.intellimate.server.database.model.Keys.KEY_APP_PRIMARY, AppVersion.APP_VERSION, "versioned_app", AppVersion.APP_VERSION.APP);
    }
}
