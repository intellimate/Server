package org.intellimate.server.jwt;

import java.util.Optional;

/**
 * @author LeanderK
 * @version 1.0
 */
public class JWTokenPassed {
    private final Subject subject;
    private final boolean refresh;
    private final int id;
    private final String app;
    private final String email;

    JWTokenPassed(Subject subject, boolean refresh, int id, String app, String email) {
        this.subject = subject;
        this.refresh = refresh;
        this.id = id;
        this.app = app;
        this.email = email;
    }

    public Subject getSubject() {
        return subject;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public int getId() {
        return id;
    }

    public Optional<String> getApp() {
        return Optional.ofNullable(app);
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }
}
