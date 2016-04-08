package org.intellimate.server;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * this class creates and reads the JWT (Json Web Token)
 * @author LeanderK
 * @version 1.0
 */
public class JWTHelper {
    private static final Logger logger = LoggerFactory.getLogger(JWTHelper.class);
    private final String secret;

    /**
     * creates the JWT-Helper
     * @param secret the base64 encoded secret to use
     */
    public JWTHelper(String secret) {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("secret must be set");
        }
        this.secret = secret;
    }

    /**
     * creates the JWT
     * @param subject the workerID
     * @return the JWT string
     */
    public String generateJWT(String subject) {
        Objects.requireNonNull(subject);
        logger.debug("encoding JWT for subject", subject);
        return Jwts.builder()
                .setSubject(subject)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * returns the Subject from the JWT
     * @param jwt the jwt to parse
     * @return the workerID
     */
    public String getSubject(String jwt) {
        String subject = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody().getSubject();
        if (subject == null) {
            logger.error("subject is null");
            throw new UnauthorizedException("subject is missing");
        }
        return subject;
    }
}
