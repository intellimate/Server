package org.intellimate.server.database.operations;

import org.intellimate.server.database.model.tables.records.UserRecord;
import org.jooq.DSLContext;
import org.jooq.Record1;

import javax.swing.text.html.Option;
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
     * returns the password for the email
     * @param email the email of the user
     * @return the password or empty if not existing
     */
    public Optional<String> getUserPassword(String email) {
        return create.select(USER.PASSWORD)
                .from(USER)
                .where(USER.EMAIL.eq(email))
                .fetchOptional()
                .map(Record1::value1);
    }

    /**
     * inserts the user into the database
     * @param userRecord the record to insert
     */
    public void insertUser(UserRecord userRecord) {
        create.executeInsert(userRecord);
    }
}
