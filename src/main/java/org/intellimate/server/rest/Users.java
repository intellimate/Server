package org.intellimate.server.rest;

import org.apache.commons.validator.routines.EmailValidator;
import org.intellimate.server.BadRequestException;
import org.intellimate.server.database.model.tables.records.UserRecord;
import org.intellimate.server.database.operations.UserOperations;
import org.intellimate.server.proto.User;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Objects;

/**
 * handles all the users requests
 * @author LeanderK
 * @version 1.0
 */
public class Users {
    private final UserOperations userOperations;

    public Users(UserOperations userOperations) {
        this.userOperations = userOperations;
    }

    /**
     * adds an user to the database
     * @param username the chosen username, must be at least 4 characters long
     * @param email the email
     * @param password the chosen password, must be at least 4 characters long
     * @throws BadRequestException if one of the parameters is invalid
     */
    public User addUser(String username, String email, String password) {
        Objects.nonNull(username);
        Objects.nonNull(email);
        Objects.nonNull(password);
        if (username.length() < 4) {
            throw new BadRequestException("invalid username length, must be at least 4: " + username);
        }
        if (password.length() < 4) {
            throw new BadRequestException("invalid password length, must be at least 4");
        }
        if (!EmailValidator.getInstance(false).isValid(email)) {
            throw new BadRequestException("invalid email: " + email);
        }
        String hashpw = BCrypt.hashpw(password, BCrypt.gensalt());
        UserRecord userRecord = new UserRecord(null, email, hashpw, username);
        int id = userOperations.insertUser(userRecord);
        return User.newBuilder()
                .setEmail(email)
                .setId(id)
                .setUsername(username)
                .build();
    }

    public void removeUser(int id) {

    }
}
