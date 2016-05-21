package org.intellimate.server.data;

import io.netty.buffer.ByteBuf;
import ratpack.stream.TransformablePublisher;

import java.util.concurrent.CompletableFuture;

/**
 * the Google cloud storage backend for FileStorage
 * @author LeanderK
 * @version 1.0
 */
public class GCS implements FileStorage {
    @Override
    public CompletableFuture<Long> save(TransformablePublisher<? extends ByteBuf> input, String name) {
        return null;
    }

    @Override
    public CompletableFuture<Long> saveExact(TransformablePublisher<? extends ByteBuf> input, String name) {
        return null;
    }

    @Override
    public void delete(String name) {

    }

    @Override
    public String getLink(String name) {
        return null;
    }

    @Override
    public String getLinkForExactName(String name) {
        return null;
    }
}
