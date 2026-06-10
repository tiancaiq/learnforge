package com.learnforge.media.storage.ali;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.*;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.CommonException;
import com.learnforge.common.utils.AssertUtils;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.media.storage.IFileStorage;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;

import static com.learnforge.media.enums.FileErrorInfo.Msg.*;

@Slf4j
public class AliFileStorage implements IFileStorage {

    private final OSS ossClient;
    private final String bucketName;

    public AliFileStorage(OSS aliOssClient, String bucketName) {
        this.ossClient = aliOssClient;
        this.bucketName = bucketName;
    }

    @Override
    public String uploadFile(String key, InputStream inputStream, long contentLength) {
        // 1. Data validation
        AssertUtils.isNotBlank(bucketName, BUCKET_NAME_IS_NULL);
        AssertUtils.isNotBlank(key, FILE_KEY_IS_NULL);
        AssertUtils.isNotNull(inputStream);
        try {
            // 2. Upload file metadata processing
            ObjectMetadata objectMeta = new ObjectMetadata();
            objectMeta.setContentLength(contentLength);
            // 3. Request parameters
            PutObjectRequest request = new PutObjectRequest(bucketName, key, inputStream, objectMeta);
            // 4. Upload
            PutObjectResult result = ossClient.putObject(request);
            ResponseMessage response = result.getResponse();
            if (!response.isSuccessful()) {
                log.info("Upload file [{}] failed, reason: {}", key, response.getErrorResponseAsString());
                throw new CommonException("Upload file failed!");
            }
            return result.getRequestId();
        } catch (Exception e) {
            log.error("Upload file [{}] failed ", key, e);
            throw new CommonException("Upload file failed!", e);
        }
    }

    @Override
    public InputStream downloadFile(String key) {
        // 1. Data validation
        AssertUtils.isNotBlank(bucketName, BUCKET_NAME_IS_NULL);
        AssertUtils.isNotBlank(key, FILE_KEY_IS_NULL);
        try {
            GetObjectRequest request = new GetObjectRequest(bucketName, key);
            return ossClient.getObject(request).getObjectContent();
        } catch (Exception e) {
            log.error("Exception occurred while downloading file [{}]:", key, e);
            throw new CommonException("File download exception.", e);
        }
    }

    @Override
    public void deleteFile(String key) {
        // 1. Data validation
        AssertUtils.isNotBlank(bucketName, BUCKET_NAME_IS_NULL);
        AssertUtils.isNotBlank(key, FILE_KEY_IS_NULL);
        try {
            // 2. Delete
            ossClient.deleteObject(bucketName, key);
        } catch (Exception e) {
            log.error("Exception occurred while deleting file [{}]:", key, e);
            throw new CommonException("Delete exception.", e);
        }
    }

    @Override
    public void deleteFiles(List<String> keys) {
        // 1. Data validation
        if(CollUtils.isEmpty(keys)){
            return;
        }
        AssertUtils.isNotBlank(bucketName, BUCKET_NAME_IS_NULL);
        if(keys.size() > 1000){
            throw new BadRequestException(FILE_KEY_TOO_MANY);
        }
        // 2. Prepare request
        DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName).withKeys(keys);
        try {
            // 3. Delete
            ossClient.deleteObjects(request);
        } catch (Exception e) {
            log.error("Exception occurred while batch deleting file [{}]:", keys, e);
            throw new CommonException("Delete exception.", e);
        }
    }
}
