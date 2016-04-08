/**
 * This class is generated by jOOQ
 */
package org.intellimate.server.database.model.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.intellimate.server.database.model.IzouServer;
import org.intellimate.server.database.model.Keys;
import org.intellimate.server.database.model.tables.records.AppVersionRecord;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.7.3"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AppVersion extends TableImpl<AppVersionRecord> {

	private static final long serialVersionUID = -2007053593;

	/**
	 * The reference instance of <code>izou_server.App_Version</code>
	 */
	public static final AppVersion APP_VERSION = new AppVersion();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<AppVersionRecord> getRecordType() {
		return AppVersionRecord.class;
	}

	/**
	 * The column <code>izou_server.App_Version.id_App_Version</code>.
	 */
	public final TableField<AppVersionRecord, Integer> ID_APP_VERSION = createField("id_App_Version", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>izou_server.App_Version.app</code>.
	 */
	public final TableField<AppVersionRecord, Integer> APP = createField("app", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>izou_server.App_Version.version</code>.
	 */
	public final TableField<AppVersionRecord, String> VERSION = createField("version", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

	/**
	 * The column <code>izou_server.App_Version.timestamp</code>.
	 */
	public final TableField<AppVersionRecord, Timestamp> TIMESTAMP = createField("timestamp", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * Create a <code>izou_server.App_Version</code> table reference
	 */
	public AppVersion() {
		this("App_Version", null);
	}

	/**
	 * Create an aliased <code>izou_server.App_Version</code> table reference
	 */
	public AppVersion(String alias) {
		this(alias, APP_VERSION);
	}

	private AppVersion(String alias, Table<AppVersionRecord> aliased) {
		this(alias, aliased, null);
	}

	private AppVersion(String alias, Table<AppVersionRecord> aliased, Field<?>[] parameters) {
		super(alias, IzouServer.IZOU_SERVER, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<AppVersionRecord> getPrimaryKey() {
		return Keys.KEY_APP_VERSION_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<AppVersionRecord>> getKeys() {
		return Arrays.<UniqueKey<AppVersionRecord>>asList(Keys.KEY_APP_VERSION_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<AppVersionRecord, ?>> getReferences() {
		return Arrays.<ForeignKey<AppVersionRecord, ?>>asList(Keys.VERSIONED_APP);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AppVersion as(String alias) {
		return new AppVersion(alias, this);
	}

	/**
	 * Rename this table
	 */
	public AppVersion rename(String name) {
		return new AppVersion(name, null);
	}
}
