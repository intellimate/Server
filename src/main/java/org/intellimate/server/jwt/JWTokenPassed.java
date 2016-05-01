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

    JWTokenPassed(Subject subject, boolean refresh, int id, String app) {
        this.subject = subject;
        this.refresh = refresh;
        this.id = id;
        this.app = app;
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
        if (app == null) {
            return Optional.empty();
        } else {
            return Optional.of(app);
        }
    }
}
