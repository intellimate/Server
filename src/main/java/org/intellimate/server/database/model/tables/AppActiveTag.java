/**
 * This class is generated by jOOQ
 */
package org.intellimate.server.database.model.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.intellimate.server.database.model.Izoudb;
import org.intellimate.server.database.model.Keys;
import org.intellimate.server.database.model.tables.records.AppActiveTagRecord;
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
public class AppActiveTag extends TableImpl<AppActiveTagRecord> {

    private static final long serialVersionUID = 28866554;

    /**
     * The reference instance of <code>izoudb.App_Active_Tag</code>
     */
    public static final AppActiveTag APP_ACTIVE_TAG = new AppActiveTag();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AppActiveTagRecord> getRecordType() {
        return AppActiveTagRecord.class;
    }

    /**
     * The column <code>izoudb.App_Active_Tag.id_App_Active_Tag</code>.
     */
    public final TableField<AppActiveTagRecord, Integer> ID_APP_ACTIVE_TAG = createField("id_App_Active_Tag", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>izoudb.App_Active_Tag.app</code>.
     */
    public final TableField<AppActiveTagRecord, Integer> APP = createField("app", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>izoudb.App_Active_Tag.tag</code>.
     */
    public final TableField<AppActiveTagRecord, Integer> TAG = createField("tag", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * Create a <code>izoudb.App_Active_Tag</code> table reference
     */
    public AppActiveTag() {
        this("App_Active_Tag", null);
    }

    /**
     * Create an aliased <code>izoudb.App_Active_Tag</code> table reference
     */
    public AppActiveTag(String alias) {
        this(alias, APP_ACTIVE_TAG);
    }

    private AppActiveTag(String alias, Table<AppActiveTagRecord> aliased) {
        this(alias, aliased, null);
    }

    private AppActiveTag(String alias, Table<AppActiveTagRecord> aliased, Field<?>[] parameters) {
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
    public Identity<AppActiveTagRecord, Integer> getIdentity() {
        return Keys.IDENTITY_APP_ACTIVE_TAG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<AppActiveTagRecord> getPrimaryKey() {
        return Keys.KEY_APP_ACTIVE_TAG_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<AppActiveTagRecord>> getKeys() {
        return Arrays.<UniqueKey<AppActiveTagRecord>>asList(Keys.KEY_APP_ACTIVE_TAG_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<AppActiveTagRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<AppActiveTagRecord, ?>>asList(Keys.APP_ACTIVE_TAG_APP_REF, Keys.APP_ACTIVE_TAG_TAG_REF);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppActiveTag as(String alias) {
        return new AppActiveTag(alias, this);
    }

    /**
     * Rename this table
     */
    public AppActiveTag rename(String name) {
        return new AppActiveTag(name, null);
    }
}
