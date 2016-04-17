package org.intellimate.server.database.operations;

import org.intellimate.server.database.model.tables.records.UserRecord;
import org.jooq.DSLContext;

import java.util.Optional;

import static org.intellimate.server.database.model.Tables.*;

/**
 * this class is concerned with the user-table
 * @author LeanderK
 * @version 1.0
 */
public class UserOperations extends AbstractOperations {
    protected UserOperations(DSLContext create) {
        super(create);
    }

    /**
     * returns the user for the email
     * @param email the email of the user
     * @return the user or empty if not existing
     */
    public Optional<UserRecord> getUser(String email) {
        return create.selectFrom(USER)
                .where(USER.EMAIL.eq(email))
                .fetchOptional();
    }

    /**
     * inserts the user into the database
     * @param userRecord the record to insert
     * @return the id of the user in the database
     */
    public int insertUser(UserRecord userRecord) {
        return create.insertInto(USER)
                .set(userRecord)
                .returning(USER.ID_USER)
                .fetchOne()
                .getIdUser();
    }

    /**
     * deletes the user form the database
     * @param userID the id of the user
     * @return true if the user existed
     */
    public boolean deleteUser(int userID) {
        return create.deleteFrom(USER)
                .where(USER.ID_USER.eq(userID))
                .execute() == 1;
    }
}