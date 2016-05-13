package org.intellimate.server.data;

import com.google.common.io.ByteStreams;
import org.intellimate.server.BadRequestException;
import org.intellimate.server.InternalServerErrorException;

import java.io.*;

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
        this.baseFileDomain = baseDomain + "/files/";
    }

    @Override
    public synchronized void save(InputStream inputStream, String name) {
        saveExact(inputStream, name+".izou");
    }

    @Override
    public void saveExact(InputStream inputStream, String name) {
        File file = new File(baseDir, name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new InternalServerErrorException(String.format("unable to create file %s", name), e);
            }
        }
        try(FileOutputStream fileInput = new FileOutputStream(file)) {
            ByteStreams.copy(inputStream, fileInput);
        } catch (IOException e) {
            throw new InternalServerErrorException(String.format("unable to save %s", name), e);
        }
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
