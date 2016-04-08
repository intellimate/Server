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
import org.intellimate.server.database.model.tables.records.UserRecord;
import org.jooq.ForeignKey;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;


/**
 * A class modelling foreign key relationships between tables of the <code>izou_server</code> 
 * schema
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.7.3"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

	// -------------------------------------------------------------------------
	// IDENTITY definitions
	// -------------------------------------------------------------------------


	// -------------------------------------------------------------------------
	// UNIQUE and PRIMARY KEY definitions
	// -------------------------------------------------------------------------

	public static final UniqueKey<AppRecord> KEY_APP_PRIMARY = UniqueKeys0.KEY_APP_PRIMARY;
	public static final UniqueKey<AppActiveTagRecord> KEY_APP_ACTIVE_TAG_PRIMARY = UniqueKeys0.KEY_APP_ACTIVE_TAG_PRIMARY;
	public static final UniqueKey<AppDependencyRecord> KEY_APP_DEPENDENCY_PRIMARY = UniqueKeys0.KEY_APP_DEPENDENCY_PRIMARY;
	public static final UniqueKey<AppInstanceRecord> KEY_APP_INSTANCE_PRIMARY = UniqueKeys0.KEY_APP_INSTANCE_PRIMARY;
	public static final UniqueKey<AppTagRecord> KEY_APP_TAG_PRIMARY = UniqueKeys0.KEY_APP_TAG_PRIMARY;
	public static final UniqueKey<AppVersionRecord> KEY_APP_VERSION_PRIMARY = UniqueKeys0.KEY_APP_VERSION_PRIMARY;
	public static final UniqueKey<DatabaseVersionRecord> KEY_DATABASE_VERSION_PRIMARY = UniqueKeys0.KEY_DATABASE_VERSION_PRIMARY;
	public static final UniqueKey<IzouInstanceRecord> KEY_IZOU_INSTANCE_PRIMARY = UniqueKeys0.KEY_IZOU_INSTANCE_PRIMARY;
	public static final UniqueKey<UserRecord> KEY_USER_PRIMARY = UniqueKeys0.KEY_USER_PRIMARY;
	public static final UniqueKey<UserRecord> KEY_USER_EMAIL_UNIQUE = UniqueKeys0.KEY_USER_EMAIL_UNIQUE;

	// -------------------------------------------------------------------------
	// FOREIGN KEY definitions
	// -------------------------------------------------------------------------

	public static final ForeignKey<AppRecord, UserRecord> APP_DEVELOPER = ForeignKeys0.APP_DEVELOPER;
	public static final ForeignKey<AppActiveTagRecord, AppRecord> APP_ACTIVE_TAG_APP_REF = ForeignKeys0.APP_ACTIVE_TAG_APP_REF;
	public static final ForeignKey<AppActiveTagRecord, AppTagRecord> APP_ACTIVE_TAG_TAG_REF = ForeignKeys0.APP_ACTIVE_TAG_TAG_REF;
	public static final ForeignKey<AppDependencyRecord, AppRecord> APP_DEPENDENCY_SUBJECT = ForeignKeys0.APP_DEPENDENCY_SUBJECT;
	public static final ForeignKey<AppDependencyRecord, AppRecord> APP_DEPENDENCY_DEPENDENCY = ForeignKeys0.APP_DEPENDENCY_DEPENDENCY;
	public static final ForeignKey<AppInstanceRecord, AppVersionRecord> APP_INSTANCE_REF = ForeignKeys0.APP_INSTANCE_REF;
	public static final ForeignKey<AppVersionRecord, AppRecord> VERSIONED_APP = ForeignKeys0.VERSIONED_APP;
	public static final ForeignKey<IzouInstanceRecord, UserRecord> IZOU_INSTANCE_USER = ForeignKeys0.IZOU_INSTANCE_USER;

	// -------------------------------------------------------------------------
	// [#1459] distribute members to avoid static initialisers > 64kb
	// -------------------------------------------------------------------------

	private static class UniqueKeys0 extends AbstractKeys {
		public static final UniqueKey<AppRecord> KEY_APP_PRIMARY = createUniqueKey(App.APP, App.APP.ID_APP);
		public static final UniqueKey<AppActiveTagRecord> KEY_APP_ACTIVE_TAG_PRIMARY = createUniqueKey(AppActiveTag.APP_ACTIVE_TAG, AppActiveTag.APP_ACTIVE_TAG.ID_APP_ACTIVE_TAG);
		public static final UniqueKey<AppDependencyRecord> KEY_APP_DEPENDENCY_PRIMARY = createUniqueKey(AppDependency.APP_DEPENDENCY, AppDependency.APP_DEPENDENCY.ID_APP_DEPENDENCY);
		public static final UniqueKey<AppInstanceRecord> KEY_APP_INSTANCE_PRIMARY = createUniqueKey(AppInstance.APP_INSTANCE, AppInstance.APP_INSTANCE.ID_APP_INSTANCE);
		public static final UniqueKey<AppTagRecord> KEY_APP_TAG_PRIMARY = createUniqueKey(AppTag.APP_TAG, AppTag.APP_TAG.ID_APP_TAGS);
		public static final UniqueKey<AppVersionRecord> KEY_APP_VERSION_PRIMARY = createUniqueKey(AppVersion.APP_VERSION, AppVersion.APP_VERSION.ID_APP_VERSION);
		public static final UniqueKey<DatabaseVersionRecord> KEY_DATABASE_VERSION_PRIMARY = createUniqueKey(DatabaseVersion.DATABASE_VERSION, DatabaseVersion.DATABASE_VERSION.ID_DATABASE_VERSION);
		public static final UniqueKey<IzouInstanceRecord> KEY_IZOU_INSTANCE_PRIMARY = createUniqueKey(IzouInstance.IZOU_INSTANCE, IzouInstance.IZOU_INSTANCE.ID_INSTANCES);
		public static final UniqueKey<UserRecord> KEY_USER_PRIMARY = createUniqueKey(User.USER, User.USER.ID_USER);
		public static final UniqueKey<UserRecord> KEY_USER_EMAIL_UNIQUE = createUniqueKey(User.USER, User.USER.EMAIL);
	}

	private static class ForeignKeys0 extends AbstractKeys {
		public static final ForeignKey<AppRecord, UserRecord> APP_DEVELOPER = createForeignKey(org.intellimate.server.database.model.Keys.KEY_USER_PRIMARY, App.APP, App.APP.DEVELOPER);
		public static final ForeignKey<AppActiveTagRecord, AppRecord> APP_ACTIVE_TAG_APP_REF = createForeignKey(org.intellimate.server.database.model.Keys.KEY_APP_PRIMARY, AppActiveTag.APP_ACTIVE_TAG, AppActiveTag.APP_ACTIVE_TAG.APP);
		public static final ForeignKey<AppActiveTagRecord, AppTagRecord> APP_ACTIVE_TAG_TAG_REF = createForeignKey(org.intellimate.server.database.model.Keys.KEY_APP_TAG_PRIMARY, AppActiveTag.APP_ACTIVE_TAG, AppActiveTag.APP_ACTIVE_TAG.TAG);
		public static final ForeignKey<AppDependencyRecord, AppRecord> APP_DEPENDENCY_SUBJECT = createForeignKey(org.intellimate.server.database.model.Keys.KEY_APP_PRIMARY, AppDependency.APP_DEPENDENCY, AppDependency.APP_DEPENDENCY.SUBJECT);
		public static final ForeignKey<AppDependencyRecord, AppRecord> APP_DEPENDENCY_DEPENDENCY = createForeignKey(org.intellimate.server.database.model.Keys.KEY_APP_PRIMARY, AppDependency.APP_DEPENDENCY, AppDependency.APP_DEPENDENCY.DEPENDENCY);
		public static final ForeignKey<AppInstanceRecord, AppVersionRecord> APP_INSTANCE_REF = createForeignKey(org.intellimate.server.database.model.Keys.KEY_APP_VERSION_PRIMARY, AppInstance.APP_INSTANCE, AppInstance.APP_INSTANCE.APP_REFERENCE);
		public static final ForeignKey<AppVersionRecord, AppRecord> VERSIONED_APP = createForeignKey(org.intellimate.server.database.model.Keys.KEY_APP_PRIMARY, AppVersion.APP_VERSION, AppVersion.APP_VERSION.APP);
		public static final ForeignKey<IzouInstanceRecord, UserRecord> IZOU_INSTANCE_USER = createForeignKey(org.intellimate.server.database.model.Keys.KEY_USER_PRIMARY, IzouInstance.IZOU_INSTANCE, IzouInstance.IZOU_INSTANCE.USER);
	}
}
