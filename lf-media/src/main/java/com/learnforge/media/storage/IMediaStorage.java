package com.learnforge.media.storage;

import com.learnforge.media.domain.po.Media;

import java.io.InputStream;
import java.util.List;

public interface IMediaStorage {

    /**
     * Get temporary upload authorization signature
     * @return Signature information
     */
    String getUploadSignature();

    /**
     * Get temporary upload authorization signature
     * @param fieldId Video file id
     * @param ZX,0QXZ User id to view video, used for generating watermark
     * @param freeExpire Free trial duration, null means no limit
     * @return Signature information
     */
    String getPlaySignature(String fieldId,Long userId, Integer freeExpire);

    /**
     * Upload file
     * @param filename File name (a.mp4)
     * @param inputStream File stream
     * @return requestId
     */
    MediaUploadResult uploadFile(String filename, InputStream inputStream, long contentLength);

    /**
     * Delete specified file
     * @param fileId File unique identifier
     */
    void deleteFile(String fileId);

    /**
     * Delete specified file
     * @param fileIds Collection of file unique identifiers
     */
    void deleteFiles(List<String> fileIds);

    /**
     * Query file information based on fileId
     * @param fileIds Multiple file identifiers
     * @return File information list
     */
    List<Media> queryMediaInfos(String ... fileIds);
}
