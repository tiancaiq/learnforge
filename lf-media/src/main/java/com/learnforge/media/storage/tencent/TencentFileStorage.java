package com.learnforge.media.storage.tencent;

import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.CommonException;
import com.learnforge.common.utils.AssertUtils;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.media.config.TencentProperties;
import com.learnforge.media.storage.IFileStorage;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.DeleteObjectsRequest.KeyVersion;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.Upload;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static com.learnforge.media.enums.FileErrorInfo.Msg.*;

@Slf4j
public class TencentFileStorage implements IFileStorage {

    private final COSClient cosClient;
    private final TransferManager transferManager;
    private final String bucketName;

    public TencentFileStorage(COSClient tencentCosClient, TransferManager transferManager, TencentProperties properties) {
        this.cosClient = tencentCosClient;
        this.transferManager = transferManager;
        this.bucketName = properties.getCos().getBucket() + "-" + properties.getAppId();
    }

    @Override
    public String uploadFile(String key, InputStream inputStream, long contentLength) {
        // 1. Data validation
        AssertUtils.isNotBlank(bucketName, BUCKET_NAME_IS_NULL);
        AssertUtils.isNotBlank(key, FILE_KEY_IS_NULL);
        AssertUtils.isNotNull(inputStream);

        // 2. Metadata, mainly file size, to enable chunked upload functionality
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(contentLength);

        // 3. Request object
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);

        try {
            // 4. Asynchronously initiate upload, return asynchronous result upload
            Upload upload = transferManager.upload(putObjectRequest);
            // 5. Wait for result
            UploadResult result = upload.waitForUploadResult();
            // 6. Return information
            return result.getRequestId();
        } catch (Exception e) {
            log.error("Exception occurred while uploading file [{}]:", key, e);
            throw new CommonException("File upload exception.", e);
        }
    }

    @Override
    public InputStream downloadFile(String key) {
        // 1. Data validation
        AssertUtils.isNotBlank(bucketName, BUCKET_NAME_IS_NULL);
        AssertUtils.isNotBlank(key, FILE_KEY_IS_NULL);
        // 2. Prepare request parameters
        GetObjectRequest request = new GetObjectRequest(bucketName, key);
        try {
            // 3. Download
            COSObject cosObject = cosClient.getObject(request);
            return cosObject.getObjectContent();
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
            cosClient.deleteObject(bucketName, key);
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
        DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName);
        // 3. Set the list of keys to delete, maximum 1000 per deletion
        List<KeyVersion> keyList = keys.stream().map(KeyVersion::new).collect(Collectors.toList());
        request.setKeys(keyList);
        try {
            // 4. Delete
            cosClient.deleteObjects(request);
        } catch (Exception e) {
            log.error("Exception occurred while batch deleting file [{}]:", keys, e);
            throw new CommonException("Delete exception.", e);
        }
    }
}
