package org.intellimate.server.data;

import io.netty.buffer.ByteBuf;
import ratpack.stream.TransformablePublisher;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

/**
 * the Google cloud storage backend for FileStorage
 * @author LeanderK
 * @version 1.0
 */
public class GCS implements FileStorage {
    @Override
    public CompletableFuture<Long> save(TransformablePublisher<? extends ByteBuf> input, String name) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public CompletableFuture<Long> saveExact(TransformablePublisher<? extends ByteBuf> input, String name) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public void delete(String name) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public String getLink(String name) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public String getLinkForExactName(String name) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public File getFileForName(String name) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }
}
