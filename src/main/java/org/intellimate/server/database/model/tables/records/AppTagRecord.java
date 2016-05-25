/**
 * This class is generated by jOOQ
 */
package org.intellimate.server.database.model.tables.records;


import javax.annotation.Generated;

import org.intellimate.server.database.model.tables.AppTag;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;


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
public class AppTagRecord extends UpdatableRecordImpl<AppTagRecord> implements Record2<Integer, String> {

    private static final long serialVersionUID = 527381155;

    /**
     * Setter for <code>izoudb.App_Tag.id_App_Tags</code>.
     */
    public void setIdAppTags(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>izoudb.App_Tag.id_App_Tags</code>.
     */
    public Integer getIdAppTags() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>izoudb.App_Tag.name</code>.
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>izoudb.App_Tag.name</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<Integer, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<Integer, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return AppTag.APP_TAG.ID_APP_TAGS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return AppTag.APP_TAG.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getIdAppTags();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppTagRecord value1(Integer value) {
        setIdAppTags(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppTagRecord value2(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppTagRecord values(Integer value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AppTagRecord
     */
    public AppTagRecord() {
        super(AppTag.APP_TAG);
    }

    /**
     * Create a detached, initialised AppTagRecord
     */
    public AppTagRecord(Integer idAppTags, String name) {
        super(AppTag.APP_TAG);

        set(0, idAppTags);
        set(1, name);
    }
}
