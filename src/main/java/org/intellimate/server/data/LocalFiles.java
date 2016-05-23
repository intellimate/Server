package org.intellimate.server.data;

import io.netty.buffer.ByteBuf;
import org.intellimate.server.BadRequestException;
import org.intellimate.server.InternalServerErrorException;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import ratpack.exec.Promise;
import ratpack.exec.Upstream;
import ratpack.stream.TransformablePublisher;

import java.io.*;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

/**
 * Saves the files on the localFile-System
 * @author LeanderK
 * @version 1.0
 */
public class LocalFiles implements FileStorage {
    private final File baseDir;
    private final String baseFileDomain;

    public LocalFiles(File baseDir, String baseDomain) {
        this.baseDir = baseDir;
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }
        this.baseFileDomain = baseDomain + "/data/";
    }

    @Override
    public CompletableFuture<Long> save(TransformablePublisher<? extends ByteBuf> input, String name) {
        return saveExact(input, name+".izou");
    }

    @Override
    public CompletableFuture<Long> saveExact(TransformablePublisher<? extends ByteBuf> input, String name) {
        File file = new File(baseDir, name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new InternalServerErrorException(String.format("unable to create file %s", name), e);
            }
        }

        CompletableFuture<Long> future = new CompletableFuture<>();

        input.subscribe(new Subscriber<ByteBuf>() {
            private Subscription subscription;
            private AsynchronousFileChannel out;
            long written;

            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                try {
                    this.out = AsynchronousFileChannel.open(
                            file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING
                    );
                    subscription.request(1);
                } catch (IOException e) {
                    subscription.cancel();
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onNext(ByteBuf byteBuf) {
                Promise.async((Upstream<Integer>)  down ->
                        out.write(byteBuf.nioBuffer(), written, null, down.completionHandler())
                ).onError(error -> {
                    byteBuf.release();
                    subscription.cancel();
                    out.close();
                    future.completeExceptionally(error);
                }).then(bytesWritten -> {
                    byteBuf.release();
                    written += bytesWritten;
                    subscription.request(1);
                });
            }

            @Override
            public void onError(Throwable t) {
                future.completeExceptionally(t);
                try {
                    out.close();
                } catch (IOException ignore) {
                    // ignore
                }
            }

            @Override
            public void onComplete() {
                try {
                    out.close();
                } catch (IOException ignore) {
                    // ignore
                }
                future.complete(written);
            }
        });

        return future;
    }

    @Override
    public synchronized void delete(String name) {
        File file = new File(baseDir, name);
        if (!file.exists()) {
            throw new BadRequestException(String.format("file %s does not exist", name));
        }
        boolean deleted = file.delete();
        if (!deleted) {
            throw new InternalServerErrorException(String.format("unable to delete file %s", name));
        }
    }

    @Override
    public synchronized String getLink(String name) {
        return baseFileDomain+name+".izou";
    }

    @Override
    public String getLinkForExactName(String name) {
        return baseFileDomain+name;
    }
}
