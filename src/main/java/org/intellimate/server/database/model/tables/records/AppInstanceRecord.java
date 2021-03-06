/**
 * This class is generated by jOOQ
 */
package org.intellimate.server.database.model.tables.records;


import javax.annotation.Generated;

import org.intellimate.server.database.model.tables.AppInstance;
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
        "jOOQ version:3.8.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AppInstanceRecord extends UpdatableRecordImpl<AppInstanceRecord> implements Record6<Integer, Integer, String, Boolean, String, String> {

    private static final long serialVersionUID = -1390091926;

    /**
     * Setter for <code>izoudb.App_Instance.id_App_Instance</code>.
     */
    public void setIdAppInstance(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>izoudb.App_Instance.id_App_Instance</code>.
     */
    public Integer getIdAppInstance() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>izoudb.App_Instance.app_reference</code>.
     */
    public void setAppReference(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>izoudb.App_Instance.app_reference</code>.
     */
    public Integer getAppReference() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>izoudb.App_Instance.platform</code>.
     */
    public void setPlatform(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>izoudb.App_Instance.platform</code>.
     */
    public String getPlatform() {
        return (String) get(2);
    }

    /**
     * Setter for <code>izoudb.App_Instance.active</code>.
     */
    public void setActive(Boolean value) {
        set(3, value);
    }

    /**
     * Getter for <code>izoudb.App_Instance.active</code>.
     */
    public Boolean getActive() {
        return (Boolean) get(3);
    }

    /**
     * Setter for <code>izoudb.App_Instance.error</code>.
     */
    public void setError(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>izoudb.App_Instance.error</code>.
     */
    public String getError() {
        return (String) get(4);
    }

    /**
     * Setter for <code>izoudb.App_Instance.warning</code>.
     */
    public void setWarning(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>izoudb.App_Instance.warning</code>.
     */
    public String getWarning() {
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
    public Row6<Integer, Integer, String, Boolean, String, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, Integer, String, Boolean, String, String> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return AppInstance.APP_INSTANCE.ID_APP_INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return AppInstance.APP_INSTANCE.APP_REFERENCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return AppInstance.APP_INSTANCE.PLATFORM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field4() {
        return AppInstance.APP_INSTANCE.ACTIVE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return AppInstance.APP_INSTANCE.ERROR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return AppInstance.APP_INSTANCE.WARNING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getIdAppInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getAppReference();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getPlatform();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value4() {
        return getActive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getError();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getWarning();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppInstanceRecord value1(Integer value) {
        setIdAppInstance(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppInstanceRecord value2(Integer value) {
        setAppReference(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppInstanceRecord value3(String value) {
        setPlatform(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppInstanceRecord value4(Boolean value) {
        setActive(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppInstanceRecord value5(String value) {
        setError(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppInstanceRecord value6(String value) {
        setWarning(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppInstanceRecord values(Integer value1, Integer value2, String value3, Boolean value4, String value5, String value6) {
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
     * Create a detached AppInstanceRecord
     */
    public AppInstanceRecord() {
        super(AppInstance.APP_INSTANCE);
    }

    /**
     * Create a detached, initialised AppInstanceRecord
     */
    public AppInstanceRecord(Integer idAppInstance, Integer appReference, String platform, Boolean active, String error, String warning) {
        super(AppInstance.APP_INSTANCE);

        set(0, idAppInstance);
        set(1, appReference);
        set(2, platform);
        set(3, active);
        set(4, error);
        set(5, warning);
    }
}
