package org.intellimate.server.data;

import java.io.InputStream;

/**
 * the Google cloud storage backend for FileStorage
 * @author LeanderK
 * @version 1.0
 */
public class GCS implements FileStorage {
    @Override
    public void save(InputStream inputStream, String name) {

    }

    @Override
    public void saveExact(InputStream inputStream, String name) {

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
