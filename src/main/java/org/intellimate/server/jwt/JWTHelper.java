package org.intellimate.server.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.intellimate.server.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * this class creates and reads the JWT (Json Web Token)
 * @author LeanderK
 * @version 1.0
 */
public class JWTHelper {
    private static final Logger logger = LoggerFactory.getLogger(JWTHelper.class);
    private static final String REFRESH_CLAIM = "refreshIzou";
    private static final String ID_CLAIM = "sid";
    private static final String APP = "app";
    private static final String EMAIL = "email";
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
                .setSubject(Subject.USER.name())
                .claim(ID_CLAIM, String.valueOf(id))
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
                .setSubject(Subject.IZOU.name())
                .claim(ID_CLAIM, String.valueOf(id))
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * creates the izou JWT-access token (has an expiration-date)
     * @param izou the id of the izou-instance
     * @param app the id of the app-instance
     * @return the JWT string
     */
    public String generateAppAccessJWT(int izou, String app) {
        logger.debug("encoding JWT for app-instance", app);
        Instant expiration = Instant.now().plus(accessTokenExpiration);
        return Jwts.builder()
                .setSubject(Subject.APP.name())
                .claim(ID_CLAIM, String.valueOf(izou))
                .claim(APP, app)
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * creates the izou JWT-refreshIzou token (has no expiration-date)
     * @param id the id of the izou-instance
     * @return the JWT string
     */
    public String generateIzouRefreshJWT(int id) {
        logger.debug("encoding JWT for izou-instance", id);
        return Jwts.builder()
                .setSubject(Subject.IZOU.name())
                .claim(ID_CLAIM, String.valueOf(id))
                .claim(REFRESH_CLAIM, Boolean.TRUE)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * generates the confirmationToken the user has to pass to the system to verify his email.
     * @param user the userID
     * @param expireDuration the duration after which the token expires
     * @param email the email to confirm
     * @return the JWT String
     */
    public String generateConfirmationToken(int user, String email, Duration expireDuration) {
        logger.debug("encoding Confirmation JWT for user", user);
        Instant expiration = Instant.now().plus(expireDuration);
        return Jwts.builder()
                .setSubject(Subject.CONFUSER.name())
                .claim(ID_CLAIM, String.valueOf(user))
                .claim(EMAIL, email)
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * generates the Reset-Token the user has to pass to the system to reset his password.
     * @param user the userID
     * @param expireDuration the duration after which the token expires
     * @return the JWT String
     */
    public String generateResetToken(int user, Duration expireDuration) {
        logger.debug("encoding Confirmation JWT for user", user);
        Instant expiration = Instant.now().plus(expireDuration);
        return Jwts.builder()
                .setSubject(Subject.RESETUSER.name())
                .claim(ID_CLAIM, String.valueOf(user))
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * parses the JWT and create the {@link JWTokenPassed}
     * @param token the jwt
     * @return the suitable JWT
     */
    public JWTokenPassed parseToken(String token) {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        Object rawId = claimsJws.getBody().get(ID_CLAIM);
        if (rawId == null) {
            throw new UnauthorizedException("Illegal JWT, no id");
        }
        int id;
        try {
            id = Integer.parseInt((String)rawId);
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("Illegal JWT, id is not an integer");
        }
        Object rawApp = claimsJws.getBody().get(APP);
        Object rawEmail = claimsJws.getBody().get(EMAIL);
        return new JWTokenPassed(Subject.valueOf(claimsJws.getBody().getSubject()),
                claimsJws.getBody().containsKey(REFRESH_CLAIM) && claimsJws.getBody().get(REFRESH_CLAIM).equals(Boolean.TRUE),
                id,
                (String)rawApp,
                (String)rawEmail);
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
