/**
 * This class is generated by jOOQ
 */
package org.intellimate.server.database.model.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.intellimate.server.database.model.Izoudb;
import org.intellimate.server.database.model.Keys;
import org.intellimate.server.database.model.tables.records.UserRecord;
import org.jooq.Field;
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
public class User extends TableImpl<UserRecord> {

    private static final long serialVersionUID = -668091813;

    /**
     * The reference instance of <code>izoudb.User</code>
     */
    public static final User USER = new User();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UserRecord> getRecordType() {
        return UserRecord.class;
    }

    /**
     * The column <code>izoudb.User.id_user</code>.
     */
    public final TableField<UserRecord, Integer> ID_USER = createField("id_user", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>izoudb.User.email</code>.
     */
    public final TableField<UserRecord, String> EMAIL = createField("email", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

    /**
     * The column <code>izoudb.User.password</code>.
     */
    public final TableField<UserRecord, String> PASSWORD = createField("password", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

    /**
     * The column <code>izoudb.User.name</code>.
     */
    public final TableField<UserRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

    /**
     * The column <code>izoudb.User.root</code>.
     */
    public final TableField<UserRecord, Boolean> ROOT = createField("root", org.jooq.impl.SQLDataType.BIT.nullable(false), this, "");

    /**
     * The column <code>izoudb.User.confirmed</code>.
     */
    public final TableField<UserRecord, Boolean> CONFIRMED = createField("confirmed", org.jooq.impl.SQLDataType.BIT.nullable(false), this, "");

    /**
     * Create a <code>izoudb.User</code> table reference
     */
    public User() {
        this("User", null);
    }

    /**
     * Create an aliased <code>izoudb.User</code> table reference
     */
    public User(String alias) {
        this(alias, USER);
    }

    private User(String alias, Table<UserRecord> aliased) {
        this(alias, aliased, null);
    }

    private User(String alias, Table<UserRecord> aliased, Field<?>[] parameters) {
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
    public Identity<UserRecord, Integer> getIdentity() {
        return Keys.IDENTITY_USER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<UserRecord> getPrimaryKey() {
        return Keys.KEY_USER_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<UserRecord>> getKeys() {
        return Arrays.<UniqueKey<UserRecord>>asList(Keys.KEY_USER_PRIMARY, Keys.KEY_USER_EMAIL_UNIQUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User as(String alias) {
        return new User(alias, this);
    }

    /**
     * Rename this table
     */
    public User rename(String name) {
        return new User(name, null);
    }
}
