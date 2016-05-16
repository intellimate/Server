package org.intellimate.server.rest;

import org.intellimate.server.BadRequestException;
import org.intellimate.server.jwt.JWTHelper;
import org.intellimate.server.NotFoundException;
import org.intellimate.server.UnauthorizedException;
import org.intellimate.server.database.model.tables.records.UserRecord;
import org.intellimate.server.database.operations.IzouInstanceOperations;
import org.intellimate.server.database.operations.UserOperations;
import org.intellimate.server.jwt.JWTokenPassed;
import org.intellimate.server.jwt.Subject;
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
        if (!user.getConfirmed()) {
            throw new BadRequestException("User is not confirmed yet");
        }
        boolean valid = BCrypt.checkpw(password, user.getPassword());
        if (valid) {
            return jwtHelper.generateUserAccessJWT(user.getIdUser());
        } else {
            throw new UnauthorizedException("wrong password for email: " + email);
        }
    }

    /**
     * handles the refreshIzou requests from the izou-instances
     * @param userId the id of the user
     * @param izouId the id of the izou-instance
     * @param app the id of the app
     * @return if valid an access jwt-token
     */
    public String app(int userId, int izouId, String app) {
        boolean valid = izouInstanceOperations.validateIzouInstanceID(izouId, userId);
        if (valid) {
            return jwtHelper.generateAppAccessJWT(izouId, app);
        } else {
            logger.info("invalid izou id {} and user {} combination", izouId, userId);
            throw new UnauthorizedException("invalid izou id "+izouId+" and user "+userId+" combination");
        }
    }

    /**
     * handles the refreshIzou requests from the izou-instances
     * @param token the refresh token
     * @return if valid an access jwt-token
     */
    public String refreshIzou(JWTokenPassed token) {
        if (!token.getSubject().equals(Subject.USER)) {
            throw new UnauthorizedException("Client needs to be an user");
        } else if (!token.isRefresh()) {
            throw new UnauthorizedException("Token must be refresh token");
        }
        boolean valid = izouInstanceOperations.validateIzouInstanceID(token.getId());
        if (valid) {
            return jwtHelper.generateIzouAccessJWT(token.getId());
        } else {
            logger.info("invalid izou id", token.getId());
            throw new UnauthorizedException("Invalid JWT, the registered instance was removed");
        }
    }
}
