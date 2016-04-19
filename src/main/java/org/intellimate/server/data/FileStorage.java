package org.intellimate.server.data;

import java.io.InputStream;

/**
 * @author LeanderK
 * @version 1.0
 */
public interface FileStorage {
    void save(InputStream inputStream, String name);
    String getLink(String name);
}
