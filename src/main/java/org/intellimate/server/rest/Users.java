package org.intellimate.server.rest;

import org.apache.commons.validator.routines.EmailValidator;
import org.intellimate.server.BadRequestException;
import org.intellimate.server.NotFoundException;
import org.intellimate.server.UnauthorizedException;
import org.intellimate.server.database.model.tables.records.UserRecord;
import org.intellimate.server.database.operations.IzouInstanceOperations;
import org.intellimate.server.database.operations.UserOperations;
import org.intellimate.server.jwt.JWTHelper;
import org.intellimate.server.jwt.JWTokenPassed;
import org.intellimate.server.jwt.Subject;
import org.intellimate.server.proto.IzouInstance;
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
    private final IzouInstanceOperations izouInstanceOperations;
    private final JWTHelper jwtHelper;

    public Users(UserOperations userOperations, IzouInstanceOperations izouInstanceOperations, JWTHelper jwtHelper) {
        this.userOperations = userOperations;
        this.izouInstanceOperations = izouInstanceOperations;
        this.jwtHelper = jwtHelper;
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

    /**
     * removes the user from the database
     * @param id the user to remove
     * @param jwTokenPassed the jwt passed
     */
    public void removeUser(int id, JWTokenPassed jwTokenPassed) {
        assertCorrectRequest(id, jwTokenPassed);
        boolean existed = userOperations.deleteUser(id);
        if (!existed) {
            throw new NotFoundException(String.format("user %d already deleted", id));
        }
    }

    /**
     * adds a new instance for the user
     * @param user the user to add the instance for
     * @param name the name of the instance
     */
    public IzouInstance addIzouInstance(int user, String name, JWTokenPassed jwTokenPassed) {
        assertCorrectRequest(user, jwTokenPassed);
        int id = izouInstanceOperations.insertIzouInstance(user, name);
        return IzouInstance.newBuilder()
                .setId(id)
                .setName(name)
                .setToken(jwtHelper.generateIzouRefreshJWT(id))
                .build();
    }

    /**
     * removes the IzouInstance if the instance belongs to the user
     * @param user the user associated
     * @param instanceId the instance id
     */
    public void removeIzouInstance(int user, int instanceId, JWTokenPassed jwTokenPassed) {
        assertCorrectRequest(user, jwTokenPassed);
        boolean existed = izouInstanceOperations.removeIzouInstance(user, instanceId);
        if (!existed) {
            throw new NotFoundException(String.format("instance was not existing for user %d", user));
        }
    }

    /**
     * asserts that the passed id is the same as the jwt id and that the jwt type is user
     * @param id the requested user id
     * @param jwTokenPassed the jwt token
     */
    public void assertCorrectRequest(int id, JWTokenPassed jwTokenPassed) {
        if (jwTokenPassed.getSubject() != Subject.USER) {
            throw new BadRequestException("method only allowed for users");
        }
        if (id != jwTokenPassed.getId()) {
            throw new UnauthorizedException("not allowed to delete other user");
        }
    }
}
