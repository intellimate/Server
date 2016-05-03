package org.intellimate.server;

import org.intellimate.server.data.FileStorage;
import org.intellimate.server.data.GCS;
import org.intellimate.server.data.LocalFiles;
import org.intellimate.server.database.DatabaseManager;
import org.intellimate.server.database.operations.AppOperations;
import org.intellimate.server.database.operations.IzouInstanceOperations;
import org.intellimate.server.database.operations.IzouOperations;
import org.intellimate.server.database.operations.UserOperations;
import org.intellimate.server.izou.Communication;
import org.intellimate.server.jwt.JWTHelper;
import org.intellimate.server.rest.AppResource;
import org.intellimate.server.rest.Authentication;
import org.intellimate.server.rest.IzouResource;
import org.intellimate.server.rest.UsersResource;
import org.jooq.SQLDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * @author LeanderK
 * @version 1.0
 */
public class Main {
    static {
        if (System.getProperty("logback.configurationFile") == null) {
            System.setProperty("logback.configurationFile", "./conf/logging.xml");
        }

        // Disable jOOQ's self-advertising
        // http://stackoverflow.com/a/28283538/2373138
        System.setProperty("org.jooq.no-logo", "true");
    }

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private final Properties file;
    private final Map<String, String> system;

    public Main() {
        String configLocation = System.getenv("server.config");
        logger.debug("Initial config-location: {}", configLocation);
        if (configLocation == null) {
            configLocation = "./conf/configuration.properties";
        }
        logger.debug("Actual config-location: {}", configLocation);
        file = new Properties();
        try {
            try {
                file.load(new FileInputStream(configLocation));
            } catch (FileNotFoundException e) {
                //used for testing
                try {
                    file.load(Main.class.getResourceAsStream(configLocation));
                } catch (NullPointerException ignored) {
                    //just means it's also not in the jar
                }
            }
        } catch (IOException e) {
            logger.error("unable to find file {}", new File(configLocation).getAbsolutePath());
            System.exit(-1);
        }
        system = System.getenv();
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.boot();
    }

    /**
     * initializes and starts the server
     */
    public void boot() {
        String url = requireProperty("database.url");
        String username = requireProperty("database.username");
        String password = requireProperty("database.password");
        String databasePool = requireProperty("database.poolName");

        SQLDialect dialect = null;
        try {
            dialect = SQLDialect.valueOf(requireProperty("database.dialect").trim());
        } catch (NullPointerException e) {
            logger.error("database.dialect not set");
            System.exit(-1);
        }
        DatabaseManager databaseManager = null;
        try {
            databaseManager = new DatabaseManager(username, password, url, databasePool, dialect, false);
        } catch (NamingException | SQLException e) {
            logger.error("unable to establish database connection", e);
            System.exit(-1);
        }

        try {
            databaseManager.initDatabase();
        } catch (SQLException e) {
            logger.error("unable to initialize database", e);
            System.exit(-1);
        }

        AppOperations appOperations = new AppOperations(databaseManager.getContext());
        IzouInstanceOperations izouInstanceOperations = new IzouInstanceOperations(databaseManager.getContext());
        IzouOperations izouOperations = new IzouOperations(databaseManager.getContext());
        UserOperations userOperations = new UserOperations(databaseManager.getContext());

        String domain = requireProperty("domain");

        FileStorage fileStorage;
        String fileDir;
        String gcs = getProperty("gcs");
        if (gcs != null) {
            fileStorage = new GCS();
            fileDir = null;
        } else {
            fileDir = requireProperty("local.filedir");
            File file = new File(fileDir);
            if (!file.exists() || !file.isDirectory()) {
                logger.error(String.format("file %s must exist and be a directory", fileDir));
                System.exit(-1);
            }
            fileStorage = new LocalFiles(file, domain);
        }

        JWTHelper jwtHelper = new JWTHelper(requireProperty("jwt.secret"));

        AppResource appResource = new AppResource(appOperations, fileStorage, userOperations);
        Authentication authentication = new Authentication(izouInstanceOperations, userOperations, jwtHelper);
        IzouResource izouResource = new IzouResource(izouOperations, userOperations, fileStorage);
        UsersResource usersResource = new UsersResource(userOperations, izouInstanceOperations, jwtHelper);

        String portRaw = getProperty("router.port");

        int port = portRaw != null
                ? Integer.parseInt(portRaw)
                : 4567;

        Communication communication = new Communication(jwtHelper, izouInstanceOperations);

        try {
            communication.startServer();
        } catch (IOException e) {
            logger.error("unable to start the server", e);
            System.exit(-1);
        }

        RatpackRouter ratpackRouter = new RatpackRouter(
                jwtHelper,
                authentication,
                usersResource,
                izouResource,
                appResource,
                communication, port,
                fileDir);

        try {
            ratpackRouter.init();
        } catch (Exception e) {
            logger.error("an error occured while trying to initialize the router", e);
        }

        logger.debug("router initialized");
    }

    /**
     * returns the system-property if present and if not falls back to the
     * config file. Exits if not existing
     * @param key the key to search for
     * @return the String
     */
    private String requireProperty(String key) {
        String result = getProperty(key);
        if (result == null) {
            logger.error(String.format("application requires key %s set", key));
            System.exit(-1);
        }
        return result;
    }

    /**
     * returns the system-property if present and if not falls back to the
     * config file
     * @param key the key to search for
     * @return the String
     */
    private String getProperty(String key) {
        String sysProperty = trimIfNotNull(system.get(key));
        if (sysProperty == null) {
            return trimIfNotNull(file.getProperty(key));
        } else {
            return trimIfNotNull(sysProperty);
        }
    }

    /**
     * returns the trimmed string if the string is not null
     * @param s the string to trim
     * @return the trimmed string or null
     */
    private String trimIfNotNull(String s) {
        if (s != null)
            return s.trim();
        else
            return s;
    }
}
