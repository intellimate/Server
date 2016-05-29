/**
 * This class is generated by jOOQ
 */
package org.intellimate.server.database.model.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.intellimate.server.database.model.Izoudb;
import org.intellimate.server.database.model.Keys;
import org.intellimate.server.database.model.tables.records.AppInstanceRecord;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Schema;
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
        "jOOQ version:3.8.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AppInstance extends TableImpl<AppInstanceRecord> {

    private static final long serialVersionUID = 77625516;

    /**
     * The reference instance of <code>izoudb.App_Instance</code>
     */
    public static final AppInstance APP_INSTANCE = new AppInstance();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AppInstanceRecord> getRecordType() {
        return AppInstanceRecord.class;
    }

    /**
     * The column <code>izoudb.App_Instance.id_App_Instance</code>.
     */
    public final TableField<AppInstanceRecord, Integer> ID_APP_INSTANCE = createField("id_App_Instance", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>izoudb.App_Instance.app_reference</code>.
     */
    public final TableField<AppInstanceRecord, Integer> APP_REFERENCE = createField("app_reference", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>izoudb.App_Instance.platform</code>.
     */
    public final TableField<AppInstanceRecord, String> PLATFORM = createField("platform", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

    /**
     * The column <code>izoudb.App_Instance.active</code>.
     */
    public final TableField<AppInstanceRecord, Boolean> ACTIVE = createField("active", org.jooq.impl.SQLDataType.BIT.nullable(false), this, "");

    /**
     * The column <code>izoudb.App_Instance.error</code>.
     */
    public final TableField<AppInstanceRecord, String> ERROR = createField("error", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>izoudb.App_Instance.warning</code>.
     */
    public final TableField<AppInstanceRecord, String> WARNING = createField("warning", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>izoudb.App_Instance</code> table reference
     */
    public AppInstance() {
        this("App_Instance", null);
    }

    /**
     * Create an aliased <code>izoudb.App_Instance</code> table reference
     */
    public AppInstance(String alias) {
        this(alias, APP_INSTANCE);
    }

    private AppInstance(String alias, Table<AppInstanceRecord> aliased) {
        this(alias, aliased, null);
    }

    private AppInstance(String alias, Table<AppInstanceRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Izoudb.IZOUDB;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<AppInstanceRecord, Integer> getIdentity() {
        return Keys.IDENTITY_APP_INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<AppInstanceRecord> getPrimaryKey() {
        return Keys.KEY_APP_INSTANCE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<AppInstanceRecord>> getKeys() {
        return Arrays.<UniqueKey<AppInstanceRecord>>asList(Keys.KEY_APP_INSTANCE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<AppInstanceRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<AppInstanceRecord, ?>>asList(Keys.APP_INSTANCE_REF);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppInstance as(String alias) {
        return new AppInstance(alias, this);
    }

    /**
     * Rename this table
     */
    public AppInstance rename(String name) {
        return new AppInstance(name, null);
    }
}
