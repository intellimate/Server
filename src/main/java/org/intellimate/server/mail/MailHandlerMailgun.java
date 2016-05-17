package org.intellimate.server.mail;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.MailBuilder;
import org.intellimate.server.jwt.JWTHelper;

import java.io.IOException;

/**
 * @author LeanderK
 * @version 1.0
 */
public class MailHandlerMailgun extends MailHandler {
    private final Configuration configuration;

    public MailHandlerMailgun(String deliveryEmailAddress, JWTHelper jwtHelper, boolean disabled, String apikey) {
        super(deliveryEmailAddress, jwtHelper, disabled);
        if (!disabled) {
            this.configuration = new Configuration()
                    .domain("izou.info")
                    .apiKey(apikey)
                    .from("Izou", deliveryEmailAddress);
        } else {
            this.configuration = null;
        }
    }

    @Override
    protected void sendMail(String receiver, String subject, String html) throws IOException {
        MailBuilder.using(configuration)
                .to(receiver)
                .subject(subject)
                .html(html)
                .build()
                .send();
    }
}
