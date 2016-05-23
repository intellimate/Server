package org.intellimate.server.database;

import org.intellimate.server.database.model.tables.records.UserRecord;
import org.intellimate.server.database.operations.IzouInstanceOperations;
import org.intellimate.server.database.operations.UserOperations;
import org.intellimate.server.rest.UsersResource;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * initializes the system with some dumnmy-data
 * @author LeanderK
 * @version 1.0
 */
public class DummyData {
    public void init(UserOperations userOperations, IzouInstanceOperations izouInstanceOperations) {
        if (!userOperations.getUser(1).isPresent()) {
            String salt = BCrypt.gensalt();
            String hashpw = BCrypt.hashpw("izou", salt);
            UserRecord userRecord = new UserRecord(1, "intellimate@izou.info", hashpw, "intellimate", true, true);
            userOperations.insertUser(userRecord);
        }
        if (!izouInstanceOperations.getInstance(1, 1).isPresent()) {
            izouInstanceOperations.insertIzouInstance(1, "test");
        }
    }
}
