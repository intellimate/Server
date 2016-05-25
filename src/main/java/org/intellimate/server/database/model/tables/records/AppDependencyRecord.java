/**
 * This class is generated by jOOQ
 */
package org.intellimate.server.database.model.tables.records;


import javax.annotation.Generated;

import org.intellimate.server.database.model.tables.AppDependency;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
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
public class AppDependencyRecord extends UpdatableRecordImpl<AppDependencyRecord> implements Record3<Integer, Integer, Integer> {

    private static final long serialVersionUID = 1586111101;

    /**
     * Setter for <code>izoudb.App_Dependency.id_App_Dependency</code>.
     */
    public void setIdAppDependency(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>izoudb.App_Dependency.id_App_Dependency</code>.
     */
    public Integer getIdAppDependency() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>izoudb.App_Dependency.subject</code>.
     */
    public void setSubject(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>izoudb.App_Dependency.subject</code>.
     */
    public Integer getSubject() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>izoudb.App_Dependency.dependency</code>.
     */
    public void setDependency(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>izoudb.App_Dependency.dependency</code>.
     */
    public Integer getDependency() {
        return (Integer) get(2);
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
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Integer, Integer, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Integer, Integer, Integer> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return AppDependency.APP_DEPENDENCY.ID_APP_DEPENDENCY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return AppDependency.APP_DEPENDENCY.SUBJECT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return AppDependency.APP_DEPENDENCY.DEPENDENCY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getIdAppDependency();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getSubject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getDependency();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppDependencyRecord value1(Integer value) {
        setIdAppDependency(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppDependencyRecord value2(Integer value) {
        setSubject(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppDependencyRecord value3(Integer value) {
        setDependency(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppDependencyRecord values(Integer value1, Integer value2, Integer value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AppDependencyRecord
     */
    public AppDependencyRecord() {
        super(AppDependency.APP_DEPENDENCY);
    }

    /**
     * Create a detached, initialised AppDependencyRecord
     */
    public AppDependencyRecord(Integer idAppDependency, Integer subject, Integer dependency) {
        super(AppDependency.APP_DEPENDENCY);

        set(0, idAppDependency);
        set(1, subject);
        set(2, dependency);
    }
}
