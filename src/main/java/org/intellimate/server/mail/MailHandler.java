package org.intellimate.server.mail;

import org.intellimate.server.jwt.JWTHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

/**
 * @author LeanderK
 * @version 1.0
 */
public abstract class MailHandler {
    private static Logger logger = LoggerFactory.getLogger(MailHandler.class);
    protected final JWTHelper jwtHelper;
    protected final String deliveryEmailAddress;
    protected final boolean disabled;

    public MailHandler(String deliveryEmailAddress, JWTHelper jwtHelper, boolean disabled) {
        this.deliveryEmailAddress = deliveryEmailAddress;
        this.jwtHelper = jwtHelper;
        this.disabled = disabled;
    }

    public void sendUserConfirmation(int user, String name, String emailAddress) throws IOException {
        if (disabled) {
            logger.info(String.format("confirmation for user %d", user));
        } else {
            Duration duration = Duration.ofHours(24);
            String jwt = jwtHelper.generateConfirmationToken(user, emailAddress, duration);

            String subject = "Please confirm your account on izou.info";
            String body = String.format("Hello, %s<br/>", name) +
                    "You're on your way!<br/>" +
                    "Let's confirm your email address.<br/>" +
                    "<br/>" +
                    "no website yet :(<br/>" +
                    String.format("<a href=\"https://api.izou.info/users/%d/confirm/%s\">click here to confirm</a><br/>", user, jwt) +
                    String.format("this link is valid for %d hours", duration.toHours());

            sendMail(emailAddress, subject, body);
        }
    }

    protected abstract void sendMail(String receiver, String subject, String html) throws IOException;

    public void sendUserPasswordReset(int user, String name, String emailAddress) throws IOException {
        if (disabled) {
            logger.info(String.format("reset password for user %d", user));
        } else {
            Duration duration = Duration.ofHours(24);
            String jwt = jwtHelper.generateResetToken(user, duration);

            String subject = "Password Reset";
            String body = String.format("Hello, %s<br/>", name) +
                    "To reset your password please visit:<br/>" +
                    "<br/>" +
                    "no website yet :(<br/>" +
                    String.format("your reset token is : %s<br/>", jwt)+
                    "you can reset your password by adding your new password to the link " +
                    String.format("https://api.izou.info/users/%d/reset-password/reset/%s/&lt;NEW PASSWORD HERE&gt; <br/>", user, jwt)+
                    String.format("this link is valid for %d hours", duration.toHours());

            sendMail(emailAddress, subject, body);
        }

    }
}
