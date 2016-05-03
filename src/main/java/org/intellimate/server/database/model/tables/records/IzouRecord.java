/**
 * This class is generated by jOOQ
 */
package org.intellimate.server.database.model.tables.records;


import javax.annotation.Generated;

import org.intellimate.server.database.model.tables.Izou;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.0"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class IzouRecord extends UpdatableRecordImpl<IzouRecord> implements Record6<Integer, Boolean, Integer, Integer, Integer, String> {

    private static final long serialVersionUID = 107528731;

    /**
     * Setter for <code>izoudb.Izou.id_Izou</code>.
     */
    public void setIdIzou(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>izoudb.Izou.id_Izou</code>.
     */
    public Integer getIdIzou() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>izoudb.Izou.active</code>.
     */
    public void setActive(Boolean value) {
        set(1, value);
    }

    /**
     * Getter for <code>izoudb.Izou.active</code>.
     */
    public Boolean getActive() {
        return (Boolean) get(1);
    }

    /**
     * Setter for <code>izoudb.Izou.major</code>.
     */
    public void setMajor(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>izoudb.Izou.major</code>.
     */
    public Integer getMajor() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>izoudb.Izou.minor</code>.
     */
    public void setMinor(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>izoudb.Izou.minor</code>.
     */
    public Integer getMinor() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>izoudb.Izou.patch</code>.
     */
    public void setPatch(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>izoudb.Izou.patch</code>.
     */
    public Integer getPatch() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>izoudb.Izou.version</code>.
     */
    public void setVersion(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>izoudb.Izou.version</code>.
     */
    public String getVersion() {
        return (String) get(5);
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
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, Boolean, Integer, Integer, Integer, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, Boolean, Integer, Integer, Integer, String> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Izou.IZOU.ID_IZOU;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field2() {
        return Izou.IZOU.ACTIVE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return Izou.IZOU.MAJOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return Izou.IZOU.MINOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field5() {
        return Izou.IZOU.PATCH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Izou.IZOU.VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getIdIzou();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value2() {
        return getActive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getMajor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getMinor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value5() {
        return getPatch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IzouRecord value1(Integer value) {
        setIdIzou(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IzouRecord value2(Boolean value) {
        setActive(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IzouRecord value3(Integer value) {
        setMajor(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IzouRecord value4(Integer value) {
        setMinor(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IzouRecord value5(Integer value) {
        setPatch(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IzouRecord value6(String value) {
        setVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IzouRecord values(Integer value1, Boolean value2, Integer value3, Integer value4, Integer value5, String value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached IzouRecord
     */
    public IzouRecord() {
        super(Izou.IZOU);
    }

    /**
     * Create a detached, initialised IzouRecord
     */
    public IzouRecord(Integer idIzou, Boolean active, Integer major, Integer minor, Integer patch, String version) {
        super(Izou.IZOU);

        set(0, idIzou);
        set(1, active);
        set(2, major);
        set(3, minor);
        set(4, patch);
        set(5, version);
    }
}
