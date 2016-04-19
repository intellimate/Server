package org.intellimate.server.rest;

import org.intellimate.server.UnauthorizedException;
import org.intellimate.server.data.FileStorage;
import org.intellimate.server.database.model.tables.records.IzouRecord;
import org.intellimate.server.database.operations.IzouOperations;
import org.intellimate.server.database.operations.UserOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

/**
 * Izou rest-operations
 * @author LeanderK
 * @version 1.0
 */
public class Izou {
    private static final Logger logger = LoggerFactory.getLogger(Izou.class);
    private final IzouOperations izouOperations;
    private final UserOperations userOperations;
    private final FileStorage fileStorage;

    public Izou(IzouOperations izouOperations, UserOperations userOperations, FileStorage fileStorage) {
        this.izouOperations = izouOperations;
        this.userOperations = userOperations;
        this.fileStorage = fileStorage;
    }

    /**
     * returns the current Izou-version
     * @return the izou-version
     */
    public org.intellimate.server.proto.Izou getCurrentVersion() {
        IzouRecord izou = izouOperations.getIzouWithHighestVersion();
        return org.intellimate.server.proto.Izou.newBuilder()
                .setId(izou.getIdIzou())
                .setVersion(String.format("%d.%d.%d", izou.getMajor(), izou.getMinor(), izou.getPatch()))
                .setDownloadLink("")
                .build();
    }

    /**
     * puts a new izou-version into the database
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     * @param userID the id of the user
     * @param request the inputStream of the request
     * @return the resulting izou instance
     */
    public org.intellimate.server.proto.Izou putIzou(int major, int minor, int patch, int userID, InputStream request) {
        Boolean isRoot = userOperations.getUser(userID)
                .orElseThrow(() -> new UnauthorizedException(String.format("user %d is not existing", userID)))
                .getRoot();
        if (!isRoot) {
            throw new UnauthorizedException(String.format("user %d is not root", userID));
        }
        IzouRecord izouRecord = izouOperations.insertIzou(major, minor, patch, false);
        String name = String.format("izou%d", izouRecord.getIdIzou());
        fileStorage.save(request, name);
        izouOperations.updateIzou(izouRecord.getIdIzou(), true);
        return org.intellimate.server.proto.Izou.newBuilder()
                .setId(izouRecord.getIdIzou())
                .setVersion(String.format("%d.%d.%d", major, minor, patch))
                .setDownloadLink(fileStorage.getLink(name))
                .build();
    }
}
