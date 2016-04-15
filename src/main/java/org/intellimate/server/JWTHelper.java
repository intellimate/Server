package org.intellimate.server;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * this class creates and reads the JWT (Json Web Token)
 * @author LeanderK
 * @version 1.0
 */
public class JWTHelper {
    private static final Logger logger = LoggerFactory.getLogger(JWTHelper.class);
    public static final String SUBJECT_IZOU = "izou";
    public static final String SUBJECT_USER = "user";
    public static final String REFRESH_CLAIM = "refresh";
    private final String secret;
    private final Duration accessTokenExpiration = Duration.ofDays(1);

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
     * creates the user JWT-access token (has an expiration-date)
     * @param id the id of the user
     * @return the JWT string
     */
    public String generateUserAccessJWT(int id) {
        logger.debug("encoding JWT for user", id);
        Instant expiration = Instant.now().plus(accessTokenExpiration);
        return Jwts.builder()
                .setSubject(SUBJECT_USER)
                .claim(SUBJECT_USER, id)
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * creates the izou JWT-access token (has an expiration-date)
     * @param id the id of the izou-instance
     * @return the JWT string
     */
    public String generateIzouAccessJWT(int id) {
        logger.debug("encoding JWT for izou-instance", id);
        Instant expiration = Instant.now().plus(accessTokenExpiration);
        return Jwts.builder()
                .setSubject(SUBJECT_IZOU)
                .claim(SUBJECT_IZOU, id)
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * creates the izou JWT-refresh token (has no expiration-date)
     * @param id the id of the izou-instance
     * @return the JWT string
     */
    public String generateIzouRefreshJWT(int id) {
        logger.debug("encoding JWT for izou-instance", id);
        return Jwts.builder()
                .setSubject(SUBJECT_IZOU)
                .claim(SUBJECT_IZOU, id)
                .claim(REFRESH_CLAIM, Boolean.TRUE)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * returns the claims from the JWT
     * @param jwt the jwt to parse
     * @return the claims
     */
    public Jws<Claims> getClaims(String jwt) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt);
    }
}
