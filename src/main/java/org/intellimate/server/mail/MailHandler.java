package org.intellimate.server.mail;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import org.intellimate.server.jwt.JWTHelper;

import java.io.IOException;
import java.time.Duration;

/**
 * @author LeanderK
 * @version 1.0
 */
public abstract class MailHandler {
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
            System.out.println(String.format("confirmation for user %d", user));
        } else {
            Duration duration = Duration.ofHours(24);
            String jwt = jwtHelper.generateConfirmationToken(user, emailAddress, duration);

            String subject = "Please confirm your account on izou.info";
            String body = String.format("Hello, %s\n", name) +
                    "You're on your way!\n" +
                    "Let's confirm your email address.\n" +
                    "\n" +
                    "no website yet :(\n" +
                    String.format("<a href=\"https://api.izou.info/users/%d/confirm/%s\">click here to confirm</a>\n", user, jwt) +
                    String.format("this link is valid for %d hours", duration.toHours());

            sendMail(emailAddress, subject, body);
        }
    }

    protected abstract void sendMail(String receiver, String subject, String html) throws IOException;

    public void sendUserPasswordReset(int user, String name, String emailAddress) throws IOException {
        if (disabled) {
            System.out.println(String.format("reset password for user %d", user));
        } else {
            Duration duration = Duration.ofHours(24);
            String jwt = jwtHelper.generateResetToken(user, duration);

            String subject = "Password Reset";
            String body = String.format("Hello, %s\n", name) +
                    "To reset your password please visit:\n" +
                    "\n" +
                    "no website yet :(\n" +
                    String.format("your reset token is : %s\n", jwt)+
                    "you can reset your password by adding your new password to the link " +
                    String.format("https://api.izou.info/users/%d/reset-password/reset/%s/<NEW PASSWORD HERE>", user, jwt)+
                    String.format("this link is valid for %d hours", duration.toHours());

            sendMail(emailAddress, subject, body);
        }

    }
}
