package org.intellimate.server.database.operations;

import org.intellimate.server.database.model.tables.records.IzouRecord;
import org.jooq.DSLContext;

import static org.intellimate.server.database.model.Tables.IZOU;

/**
 * this class is concerned with operations on the Izou Table
 * @author LeanderK
 * @version 1.0
 */
public class IzouOperations extends AbstractOperations {
    public IzouOperations(DSLContext create) {
        super(create);
    }
    public IzouRecord insertIzou(int major, int minor, int patch, boolean active) {
        return create.insertInto(IZOU)
                .set(new IzouRecord(null, active, major, minor, patch, String.format("%d.%d.%d", major, minor, patch)))
                .returning()
                .fetchOne();
    }

    public IzouRecord updateIzou(int id, boolean active) {
        return create.update(IZOU)
                .set(IZOU.ACTIVE, active)
                .where(IZOU.ID_IZOU.eq(id))
                .returning()
                .fetchOne();
    }

    public IzouRecord getIzouWithHighestVersion() {
        return create.selectFrom(IZOU)
                .where(IZOU.ACTIVE.eq(true))
                .orderBy(IZOU.VERSION.desc())
                .limit(1)
                .fetchOne();
    }
}
