package org.intellimate.server.database.operations;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import static org.intellimate.server.database.model.Tables.*;

/**
 * contains all the operations concerning the izou-instance table
 * @author LeanderK
 * @version 1.0
 */
public class IzouInstanceOperations extends AbstractOperations {

    /**
     * creates a new IzouInstanceOperations
     * @param context the create to use
     */
    protected IzouInstanceOperations(DSLContext context) {
        super(context);
    }

    /**
     * validates that the passed id is existing in the db
     * @param id the id to validate
     * @return true if valid, false if not
     */
    public boolean validateIzouInstanceID(int id) {
        return create.fetchExists(
                DSL.select(IZOU_INSTANCE.ID_INSTANCES)
                    .where(IZOU_INSTANCE.ID_INSTANCES.eq(id))
        );
    }
}
