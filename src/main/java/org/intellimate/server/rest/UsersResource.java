package org.intellimate.server.rest;

import com.sendgrid.SendGridException;
import org.apache.commons.validator.routines.EmailValidator;
import org.intellimate.server.*;
import org.intellimate.server.database.model.tables.records.UserRecord;
import org.intellimate.server.database.operations.AppOperations;
import org.intellimate.server.database.operations.IzouInstanceOperations;
import org.intellimate.server.database.operations.UserOperations;
import org.intellimate.server.jwt.JWTHelper;
import org.intellimate.server.jwt.JWTokenPassed;
import org.intellimate.server.jwt.Subject;
import org.intellimate.server.mail.MailHandler;
import org.intellimate.server.proto.App;
import org.intellimate.server.proto.AppList;
import org.intellimate.server.proto.IzouInstance;
import org.intellimate.server.proto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * handles all the users requests
 * @author LeanderK
 * @version 1.0
 */
public class UsersResource {
    private final UserOperations userOperations;
    private final IzouInstanceOperations izouInstanceOperations;
    private final AppOperations appOperations;
    private final JWTHelper jwtHelper;
    private final MailHandler mailHandler;
    private final String salt = BCrypt.gensalt();
    private static Logger logger = LoggerFactory.getLogger(UsersResource.class);

    public UsersResource(UserOperations userOperations, IzouInstanceOperations izouInstanceOperations, AppOperations appOperations, JWTHelper jwtHelper, MailHandler mailHandler) {
        this.userOperations = userOperations;
        this.izouInstanceOperations = izouInstanceOperations;
        this.appOperations = appOperations;
        this.jwtHelper = jwtHelper;
        this.mailHandler = mailHandler;
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
        String hashpw = BCrypt.hashpw(password, salt);
        UserRecord userRecord = new UserRecord(null, email, hashpw, username, false, false);
        int id = userOperations.insertUser(userRecord);
        try {
            mailHandler.sendUserConfirmation(id, username, email);
        } catch (IOException e) {
            logger.info("unable to send email", e);
            throw new InternalServerErrorException("unable to send confirmation email", e);
        }
        return User.newBuilder()
                .setEmail(email)
                .setId(id)
                .setUsername(username)
                .build();
    }

    /**
     * resends the Confirmation-email
     * @param email the email address of the suspected user
     */
    public String resendConfirmationEmail(String email) {
        UserRecord account = userOperations.getUser(email)
                .orElseThrow(() -> new NotFoundException("User is not existing"));
        Boolean confirmed = account
                .getConfirmed();
        if (confirmed) {
            throw new BadRequestException("Account is already confirmed");
        }
        try {
            mailHandler.sendUserConfirmation(account.getIdUser(), account.getName(), account.getEmail());
        } catch (IOException e) {
            logger.info("unable to send email", e);
            throw new InternalServerErrorException("unable to send confirmation email", e);
        }
        return "Ok";
    }

    /***
     * sends an email to reset the password
     * @param email the email address
     */
    public String sendPasswordResetEmail(String email) {
        UserRecord account = userOperations.getUser(email)
                .orElseThrow(() -> new NotFoundException("User is not existing"));
        Boolean confirmed = account
                .getConfirmed();
        if (!confirmed) {
            throw new BadRequestException("Account is not activated yet");
        }
        try {
            mailHandler.sendUserPasswordReset(account.getIdUser(), account.getName(), account.getEmail());
        } catch (IOException e) {
            logger.info("unable to send email", e);
            throw new InternalServerErrorException("unable to send reset email", e);
        }
        return "Ok";
    }

    /**
     * confirms the account
     * @param token the JWToken with the Type CONFUSER
     */
    public String confirmUser(JWTokenPassed token) {
        if (!Subject.CONFUSER.equals(token.getSubject())) {
            throw new BadRequestException("wrong token");
        }
        String email = token.getEmail().orElseThrow(() -> new InternalServerErrorException("illegal token generated"));
        UserRecord account = userOperations.getUser(email)
                .orElseThrow(() -> new NotFoundException("User is not existing"));
        if (!email.equals(account.getEmail())) {
            throw new BadRequestException("email changed, now active: "+account.getEmail()+", requested"+email);
        }
        UserRecord userRecord = new UserRecord();
        userRecord.setConfirmed(true);
        userOperations.updateUser(token.getId(), userRecord);
        return "Ok";
    }

    /**
     * resets the password of for the account
     * @param token the token passed
     * @param password the password to change to
     */
    public String resetPassword(JWTokenPassed token, String password) {
        if (!Subject.RESETUSER.equals(token.getSubject())) {
            throw new BadRequestException("wrong token");
        }
        if (password.length() < 4) {
            throw new BadRequestException("invalid password length, must be at least 4");
        }
        String hashpw = BCrypt.hashpw(password, salt);
        UserRecord userRecord = new UserRecord();
        userRecord.setPassword(hashpw);
        userOperations.updateUser(token.getId(), userRecord);
        return "Ok";
    }

    /**
     * patches the user-record
     * @param user the new data
     * @param id the id of the user
     * @return the updated User
     */
    public User patchUser(User user, int id) {
        UserRecord existing = userOperations.getUser(id)
                .orElseThrow(() -> new NotFoundException("User is not existing"));

        boolean changedMail = false;
        if (!user.getUsername().equals(existing.getName())) {
            existing.setName(user.getUsername());
        }
        if (!user.getEmail().equals(existing.getEmail())) {
            existing.setEmail(user.getEmail());
            existing.setConfirmed(false);
            changedMail = true;
        }
        boolean changedPassword = BCrypt.checkpw(user.getPassword(), existing.getPassword());
        if (changedPassword) {
            String hashpw = BCrypt.hashpw(user.getPassword(), salt);
            existing.setPassword(hashpw);
        }
        UserRecord changed = userOperations.updateUser(id, existing);
        if (changedMail) {
            try {
                mailHandler.sendUserConfirmation(id, user.getUsername(), user.getEmail());
            } catch (IOException e) {
                logger.info("unable to send email", e);
                throw new InternalServerErrorException("unable to send confirmation email", e);
            }
        }
        return User.newBuilder()
                .setUsername(changed.getName())
                .setId(id)
                .setEmail(changed.getEmail())
                .build();
    }

    /**
     * returns the user for the id
     * @param jwtID the authorized id
     * @param id the requested id
     * @return the user
     */
    public User getUser(int jwtID, int id) {
        if (jwtID != id) {
            throw new UnauthorizedException("Not allowed to request user "+id);
        }
        return userOperations.getUser(id)
                .map(record ->
                        User.newBuilder()
                                .setUsername(record.getName())
                                .setId(id)
                                .setEmail(record.getEmail())
                                .build()
                )
                .orElseThrow(() -> new BadRequestException("user not existing: ", id));
    }

    /**
     * removes the user from the database
     * @param id the user to remove
     * @param jwTokenPassed the jwt passed
     */
    public String removeUser(int id, JWTokenPassed jwTokenPassed) {
        assertCorrectRequest(id, jwTokenPassed);
        boolean existed = userOperations.deleteUser(id);
        if (!existed) {
            throw new NotFoundException(String.format("user %d already deleted", id));
        }
        return "Ok";
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
     * returns all the apps where the user is the developer responsible
     * @param user the user
     * @return a list of apps
     */
    public AppList getUsersApps(int user) {
        return AppList.newBuilder()
                .addAllApps(
                        appOperations.getUsersApps(user).stream()
                                .map(record -> App.newBuilder()
                                        .setId(record.getIdApp())
                                        .setName(record.getName())
                                        .setActive(record.getActive())
                                        .setDescription(record.getDescription())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }

    /**
     * removes the IzouInstance if the instance belongs to the user
     * @param user the user associated
     * @param instanceId the instance id
     */
    public String removeIzouInstance(int user, int instanceId, JWTokenPassed jwTokenPassed) {
        assertCorrectRequest(user, jwTokenPassed);
        boolean existed = izouInstanceOperations.removeIzouInstance(user, instanceId);
        if (!existed) {
            throw new NotFoundException(String.format("instance was not existing for user %d", user));
        }
        return "Ok";
    }

    /**
     * asserts that the passed id is the same as the jwt id and that the jwt type is user
     * @param id the requested user id
     * @param jwTokenPassed the jwt token
     */
    public String assertCorrectRequest(int id, JWTokenPassed jwTokenPassed) {
        if (jwTokenPassed.getSubject() != Subject.USER) {
            throw new BadRequestException("method only allowed for users");
        }
        if (id != jwTokenPassed.getId()) {
            throw new UnauthorizedException("not allowed to delete other user");
        }
        return "Ok";
    }

    /**
     * returns an Izou-Instance
     * @param userId the user
     * @return the izou-instance
     */
    public List<IzouInstance> getIzouInstances(int userId) {
        return izouInstanceOperations.getAllInstancesForUser(userId).stream()
                .map(record -> IzouInstance.newBuilder().setName(record.getName()).setId(record.getIdInstances()).build())
                .collect(Collectors.toList());
    }

    /**
     * returns an Izou-Instance
     * @param userId the user
     * @param izouid the izou-id
     * @return the izou-instance
     */
    public IzouInstance getIzouInstance(int userId, int izouid) {
        return izouInstanceOperations.getInstance(userId, izouid)
                .map(record ->
                        IzouInstance.newBuilder()
                                .setId(record.getIdInstances())
                                .setName(record.getName())
                                .setToken(jwtHelper.generateIzouRefreshJWT(record.getIdInstances()))
                                .build()
                )
                .orElseThrow(() -> new NotFoundException(String.format("No Izou-instance found for user %d and izou-id %d", userId, izouid)));
    }
}
