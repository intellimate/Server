package org.intellimate.server.data;

import io.netty.buffer.ByteBuf;
import ratpack.stream.TransformablePublisher;

import java.util.concurrent.CompletableFuture;

/**
 * @author LeanderK
 * @version 1.0
 */
public interface FileStorage {
    CompletableFuture<Long> save(TransformablePublisher<? extends ByteBuf> input, String name);
    CompletableFuture<Long> saveExact(TransformablePublisher<? extends ByteBuf> input, String name);
    void delete(String name);
    String getLink(String name);
    String getLinkForExactName(String name);
}
