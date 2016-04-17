package org.intellimate.server.database.operations;

import org.jooq.DSLContext;

import static org.intellimate.server.database.model.Tables.*;

/**
 * base class for all operations
 * @author LeanderK
 * @version 1.0
 */
public abstract class AbstractOperations {
    protected final DSLContext create;

    protected AbstractOperations(DSLContext create) {
        this.create = create;
    }
}