package org.intellimate.server.jwt;

/**
 * @author LeanderK
 * @version 1.0
 */
public class JWTokenPassed {
    private final Subject subject;
    private final boolean refresh;
    private final int id;

    JWTokenPassed(Subject subject, boolean refresh, int id) {
        this.subject = subject;
        this.refresh = refresh;
        this.id = id;
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
}
