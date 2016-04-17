package org.intellimate.server.database.operations;

import org.intellimate.server.database.model.tables.records.IzouInstanceRecord;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import static org.intellimate.server.database.model.Tables.*;

/**
 * contains all the operations concerning the izou-instance table
 * @author LeanderK
 * @version 1.0
 */
public class IzouInstanceOperations extends AbstractOperations {

    /**
     * creates a new IzouInstanceOperations
     * @param context the create to use
     */
    protected IzouInstanceOperations(DSLContext context) {
        super(context);
    }

    /**
     * validates that the passed id is existing in the db
     * @param id the id to validate
     * @return true if valid, false if not
     */
    public boolean validateIzouInstanceID(int id) {
        return create.fetchExists(
                DSL.select(IZOU_INSTANCE.ID_INSTANCES)
                    .where(IZOU_INSTANCE.ID_INSTANCES.eq(id))
        );
    }

    /**
     * inserts the izou instance the database
     * @param user the user the instance associated with
     * @param name the name of the instance
     * @return the id of the instance
     */
    public int insertIzouInstance(int user, String name) {
        return create.insertInto(IZOU_INSTANCE)
                .set(new IzouInstanceRecord(null, user, name))
                .returning(IZOU_INSTANCE.ID_INSTANCES)
                .fetchOne()
                .getIdInstances();
    }

    /**
     * deletes the izou-instance from the database
     * @param user the user associated
     * @param izouInstance the instance id
     * @return true if existed and tied to the user
     */
    public boolean removeIzouInstance(int user, int izouInstance) {
        return create.deleteFrom(IZOU_INSTANCE)
                .where(IZOU_INSTANCE.ID_INSTANCES.eq(izouInstance))
                .and(IZOU_INSTANCE.USER.eq(user))
                .execute() == 1;
    }
}
