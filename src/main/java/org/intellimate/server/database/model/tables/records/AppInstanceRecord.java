/**
 * This class is generated by jOOQ
 */
package org.intellimate.server.database.model.tables.records;


import javax.annotation.Generated;

import org.intellimate.server.database.model.tables.AppInstance;
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
public class AppInstanceRecord extends UpdatableRecordImpl<AppInstanceRecord> implements Record3<Integer, Integer, String> {

	private static final long serialVersionUID = 745085026;

	/**
	 * Setter for <code>izou_server.App_Instance.id_App_Instance</code>.
	 */
	public void setIdAppInstance(Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>izou_server.App_Instance.id_App_Instance</code>.
	 */
	public Integer getIdAppInstance() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>izou_server.App_Instance.app_reference</code>.
	 */
	public void setAppReference(Integer value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>izou_server.App_Instance.app_reference</code>.
	 */
	public Integer getAppReference() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>izou_server.App_Instance.platform</code>.
	 */
	public void setPlatform(String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>izou_server.App_Instance.platform</code>.
	 */
	public String getPlatform() {
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
	public AppInstanceRecord values(Integer value1, Integer value2, String value3) {
		value1(value1);
		value2(value2);
		value3(value3);
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
	public AppInstanceRecord(Integer idAppInstance, Integer appReference, String platform) {
		super(AppInstance.APP_INSTANCE);

		setValue(0, idAppInstance);
		setValue(1, appReference);
		setValue(2, platform);
	}
}
