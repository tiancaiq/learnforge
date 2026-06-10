package com.learnforge.media.storage;

import java.io.InputStream;
import java.util.List;

public interface IFileStorage {

    /**
     * Upload file
     * @param key File unique identifier (a.jpg)
     * @param inputStream File stream
     * @return requestId
     */
    String uploadFile(String key, InputStream inputStream, long contentLength);

    /**
     * Download file
     * @param key File unique identifier (a.jpg)
     * @return File stream
     */
    InputStream downloadFile(String key);

    /**
     * Delete specified file
     * @param key File unique identifier (a.jpg)
     */
    void deleteFile(String key);

    /**
     * Delete specified file
     * @param keys Collection of file unique identifiers (a.jpg)
     */
    void deleteFiles(List<String> keys);
}
