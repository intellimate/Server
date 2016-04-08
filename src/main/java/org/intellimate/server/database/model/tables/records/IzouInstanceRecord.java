/**
 * This class is generated by jOOQ
 */
package org.intellimate.server.database.model.tables.records;


import javax.annotation.Generated;

import org.intellimate.server.database.model.tables.IzouInstance;
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
		"jOOQ version:3.7.3"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class IzouInstanceRecord extends UpdatableRecordImpl<IzouInstanceRecord> implements Record3<Integer, Integer, String> {

	private static final long serialVersionUID = 1157702049;

	/**
	 * Setter for <code>izou_server.Izou_Instance.id_Instances</code>.
	 */
	public void setIdInstances(Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>izou_server.Izou_Instance.id_Instances</code>.
	 */
	public Integer getIdInstances() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>izou_server.Izou_Instance.user</code>.
	 */
	public void setUser(Integer value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>izou_server.Izou_Instance.user</code>.
	 */
	public Integer getUser() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>izou_server.Izou_Instance.name</code>.
	 */
	public void setName(String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>izou_server.Izou_Instance.name</code>.
	 */
	public String getName() {
		return (String) getValue(2);
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
	public Row3<Integer, Integer, String> fieldsRow() {
		return (Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Integer, Integer, String> valuesRow() {
		return (Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return IzouInstance.IZOU_INSTANCE.ID_INSTANCES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return IzouInstance.IZOU_INSTANCE.USER;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return IzouInstance.IZOU_INSTANCE.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getIdInstances();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getUser();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IzouInstanceRecord value1(Integer value) {
		setIdInstances(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IzouInstanceRecord value2(Integer value) {
		setUser(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IzouInstanceRecord value3(String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IzouInstanceRecord values(Integer value1, Integer value2, String value3) {
		value1(value1);
		value2(value2);
		value3(value3);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached IzouInstanceRecord
	 */
	public IzouInstanceRecord() {
		super(IzouInstance.IZOU_INSTANCE);
	}

	/**
	 * Create a detached, initialised IzouInstanceRecord
	 */
	public IzouInstanceRecord(Integer idInstances, Integer user, String name) {
		super(IzouInstance.IZOU_INSTANCE);

		setValue(0, idInstances);
		setValue(1, user);
		setValue(2, name);
	}
}
