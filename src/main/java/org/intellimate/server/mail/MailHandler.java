package org.intellimate.server.mail;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import org.intellimate.server.jwt.JWTHelper;

import java.time.Duration;

/**
 * @author LeanderK
 * @version 1.0
 */
public class MailHandler {
    private final JWTHelper jwtHelper;
    private final SendGrid sendgrid;
    private final String deliveryEmailAddress;
    private final boolean disabled;

    public MailHandler(JWTHelper jwtHelper, boolean disabled, String sendGridAPIKey, String deliveryEmailAddress) {
        this.jwtHelper = jwtHelper;
        this.disabled = disabled;
        this.sendgrid = new SendGrid(sendGridAPIKey);
        this.deliveryEmailAddress = deliveryEmailAddress;
    }

    public void sendUserConfirmation(int user, String name, String emailAddress) throws SendGridException {
        if (disabled) {
            System.out.println(String.format("confirmation for user %d", user));
        } else {
            Duration duration = Duration.ofHours(24);
            String jwt = jwtHelper.generateConfirmationToken(user, emailAddress, duration);
            SendGrid.Email email = new SendGrid.Email();

            email.addTo(emailAddress);
            email.setFrom(deliveryEmailAddress);
            email.setSubject("Please confirm your account on izou.info");
            email.setHtml(String.format("Hello, %s\n", name)+
                    "You're on your way!\n" +
                    "Let's confirm your email address.\n" +
                    "\n" +
                    "not website yet :(\n" +
                    String.format("<a href=\"https://api.izou.info/users/%d/confirm/%s\">click here to confirm</a>\n", user, jwt)+
                    String.format("this link is valid for %d hours", duration.toHours()));

            SendGrid.Response response = sendgrid.send(email);
        }

    }

    public void sendUserPasswordReset(int user, String name, String emailAddress) throws SendGridException {
        if (disabled) {
            System.out.println(String.format("reset password for user %d", user));
        } else {
            Duration duration = Duration.ofHours(24);
            String jwt = jwtHelper.generateResetToken(user, duration);
        }

    }
}
