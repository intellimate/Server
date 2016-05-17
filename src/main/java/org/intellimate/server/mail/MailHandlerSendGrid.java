package org.intellimate.server.mail;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import org.intellimate.server.jwt.JWTHelper;

import java.io.IOException;

/**
 * @author LeanderK
 * @version 1.0
 */
public class MailHandlerSendGrid extends MailHandler {
    private final SendGrid sendgrid;

    public MailHandlerSendGrid(JWTHelper jwtHelper, boolean disabled, String sendGridAPIKey, String deliveryEmailAddress) {
        super(deliveryEmailAddress, jwtHelper, disabled);
        this.sendgrid = new SendGrid(sendGridAPIKey);
    }

    @Override
    protected void sendMail(String receiver, String subject, String html) throws IOException {
        SendGrid.Email email = new SendGrid.Email();

        email.addTo(receiver);
        email.setFrom(deliveryEmailAddress);
        email.setSubject(subject);
        email.setHtml(html);

        try {
            SendGrid.Response response = sendgrid.send(email);
        } catch (SendGridException e) {
            throw new IOException(e);
        }
    }
}
