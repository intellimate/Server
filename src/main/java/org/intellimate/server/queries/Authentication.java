package org.intellimate.server.queries;

import org.apache.commons.validator.routines.EmailValidator;
import org.intellimate.server.BadRequestException;
import org.intellimate.server.JWTHelper;
import org.intellimate.server.NotFoundException;
import org.intellimate.server.UnauthorizedException;
import org.intellimate.server.database.model.tables.records.UserRecord;
import org.intellimate.server.database.operations.IzouInstanceOperations;
import org.intellimate.server.database.operations.UserOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Objects;

/**
 * handles all the authentication requests
 * @author LeanderK
 * @version 1.0
 */
public class Authentication {
    private static final Logger logger = LoggerFactory.getLogger(Authentication.class);
    private final IzouInstanceOperations izouInstanceOperations;
    private final UserOperations userOperations;
    private final JWTHelper jwtHelper;

    public Authentication(IzouInstanceOperations izouInstanceOperations, UserOperations userOperations, JWTHelper jwtHelper) {
        this.izouInstanceOperations = izouInstanceOperations;
        this.userOperations = userOperations;
        this.jwtHelper = jwtHelper;
    }

    /**
     * handles the login request for the users
     * @param email the email
     * @param password the password
     * @return if valid an access jwt-token
     */
    public String login(String email, String password) {
        Objects.nonNull(email);
        Objects.nonNull(password);
        UserRecord user = userOperations.getUser(email)
                .orElseThrow(() -> new NotFoundException("no user found for email: " + email));
        boolean valid = BCrypt.checkpw(password, user.getPassword());
        if (valid) {
            return jwtHelper.generateUserAccessJWT(user.getIdUser());
        } else {
            throw new UnauthorizedException("wrong password for email: " + email);
        }
    }

    /**
     * adds an user to the database
     * @param username the chosen username, must be at least 4 characters long
     * @param email the email
     * @param password the chosen password, must be at least 4 characters long
     * @throws BadRequestException if one of the parameters is invalid
     */
    public void addUser(String username, String email, String password) {
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
        userOperations.insertUser(userRecord);
    }

    /**
     * handles the refresh requests from the izou-instances
     * @param id the id of the izou-instance from the refresh-token
     * @return if valid an access jwt-token
     */
    public String refresh(String id) {
        try {
            int parsedID = Integer.parseInt(id);
            boolean valid = izouInstanceOperations.validateIzouInstanceID(parsedID);
            if (valid) {
                return jwtHelper.generateIzouAccessJWT(parsedID);
            } else {
                logger.info("invalid izou id", id);
                throw new UnauthorizedException("Invalid JWT, the registered instance was removed");
            }
        } catch (NumberFormatException e) {
            logger.info("unable to parse {} as an integer", id);
            throw new UnauthorizedException("Invalid JWT");
        }
    }
}
