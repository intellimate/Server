package org.intellimate.server.database.operations;

import org.intellimate.server.database.model.tables.records.IzouInstanceRecord;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.Optional;

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
    public IzouInstanceOperations(DSLContext context) {
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
     * validates that the passed id is existing in the db
     * @param izouID the izou id to validate
     * @param userID the user belonging to the instance
     * @return true if valid, false if not
     */
    public boolean validateIzouInstanceID(int izouID, int userID) {
        return create.fetchExists(
                DSL.select(IZOU_INSTANCE.ID_INSTANCES)
                        .where(IZOU_INSTANCE.ID_INSTANCES.eq(izouID))
                        .and(IZOU_INSTANCE.USER.eq(userID))
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

    /**
     * returns all the instances for the user
     * @param user the user
     * @return a list of the instances
     */
    public List<IzouInstanceRecord> getAllInstancesForUser(int user) {
        return create.selectFrom(IZOU_INSTANCE)
                .where(IZOU_INSTANCE.USER.eq(user))
                .fetch();
    }

    /**
     * returns a matching izou-instance or empty
     * @param user the user to check for
     * @param izouId the izou-id
     * @return the record or null
     */
    public Optional<IzouInstanceRecord> getInstance(int user, int izouId) {
        return create.selectFrom(IZOU_INSTANCE)
                .where(IZOU_INSTANCE.USER.eq(user))
                .and(IZOU_INSTANCE.ID_INSTANCES.eq(izouId))
                .fetchOptional();
    }
}
