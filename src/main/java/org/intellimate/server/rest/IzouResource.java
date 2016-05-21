package org.intellimate.server.rest;

import io.netty.buffer.ByteBuf;
import org.intellimate.server.UnauthorizedException;
import org.intellimate.server.data.FileStorage;
import org.intellimate.server.database.model.tables.records.IzouRecord;
import org.intellimate.server.database.operations.IzouOperations;
import org.intellimate.server.database.operations.UserOperations;
import org.intellimate.server.proto.Izou;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Promise;
import ratpack.stream.TransformablePublisher;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

/**
 * Izou rest-operations
 * @author LeanderK
 * @version 1.0
 */
public class IzouResource {
    private static final Logger logger = LoggerFactory.getLogger(IzouResource.class);
    private final IzouOperations izouOperations;
    private final UserOperations userOperations;
    private final FileStorage fileStorage;

    public IzouResource(IzouOperations izouOperations, UserOperations userOperations, FileStorage fileStorage) {
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
    public Promise<org.intellimate.server.proto.Izou> putIzou(int major, int minor, int patch, int userID, TransformablePublisher<? extends ByteBuf> request) {
        Boolean isRoot = userOperations.getUser(userID)
                .orElseThrow(() -> new UnauthorizedException(String.format("user %d is not existing", userID)))
                .getRoot();
        if (!isRoot) {
            throw new UnauthorizedException(String.format("user %d is not root", userID));
        }
        IzouRecord izouRecord = izouOperations.insertIzou(major, minor, patch, false);
        String name = String.format("izou%d", izouRecord.getIdIzou());
        CompletableFuture<Long> future = fileStorage.save(request, name);

        return Promise.of(down -> down.accept(future))
                .map(ignore -> {
                    izouOperations.updateIzou(izouRecord.getIdIzou(), true);
                    return Izou.newBuilder()
                            .setId(izouRecord.getIdIzou())
                            .setVersion(String.format("%d.%d.%d", major, minor, patch))
                            .setDownloadLink(fileStorage.getLink(name))
                            .build();
                });
    }
}
