/**
 * This class is generated by jOOQ
 */
package org.intellimate.server.database.model;


import javax.annotation.Generated;

import org.intellimate.server.database.model.tables.App;
import org.intellimate.server.database.model.tables.AppActiveTag;
import org.intellimate.server.database.model.tables.AppDependency;
import org.intellimate.server.database.model.tables.AppInstance;
import org.intellimate.server.database.model.tables.AppTag;
import org.intellimate.server.database.model.tables.AppVersion;
import org.intellimate.server.database.model.tables.DatabaseVersion;
import org.intellimate.server.database.model.tables.Izou;
import org.intellimate.server.database.model.tables.IzouInstance;
import org.intellimate.server.database.model.tables.User;


/**
 * Convenience access to all tables in izoudb
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.8.0"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>izoudb.App</code>.
     */
    public static final App APP = org.intellimate.server.database.model.tables.App.APP;

    /**
     * The table <code>izoudb.App_Active_Tag</code>.
     */
    public static final AppActiveTag APP_ACTIVE_TAG = org.intellimate.server.database.model.tables.AppActiveTag.APP_ACTIVE_TAG;

    /**
     * The table <code>izoudb.App_Dependency</code>.
     */
    public static final AppDependency APP_DEPENDENCY = org.intellimate.server.database.model.tables.AppDependency.APP_DEPENDENCY;

    /**
     * The table <code>izoudb.App_Instance</code>.
     */
    public static final AppInstance APP_INSTANCE = org.intellimate.server.database.model.tables.AppInstance.APP_INSTANCE;

    /**
     * The table <code>izoudb.App_Tag</code>.
     */
    public static final AppTag APP_TAG = org.intellimate.server.database.model.tables.AppTag.APP_TAG;

    /**
     * The table <code>izoudb.App_Version</code>.
     */
    public static final AppVersion APP_VERSION = org.intellimate.server.database.model.tables.AppVersion.APP_VERSION;

    /**
     * The table <code>izoudb.Database_Version</code>.
     */
    public static final DatabaseVersion DATABASE_VERSION = org.intellimate.server.database.model.tables.DatabaseVersion.DATABASE_VERSION;

    /**
     * The table <code>izoudb.Izou</code>.
     */
    public static final Izou IZOU = org.intellimate.server.database.model.tables.Izou.IZOU;

    /**
     * The table <code>izoudb.Izou_Instance</code>.
     */
    public static final IzouInstance IZOU_INSTANCE = org.intellimate.server.database.model.tables.IzouInstance.IZOU_INSTANCE;

    /**
     * The table <code>izoudb.User</code>.
     */
    public static final User USER = org.intellimate.server.database.model.tables.User.USER;
}
