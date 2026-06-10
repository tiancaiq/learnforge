package com.learnforge.media.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import com.qcloud.vod.VodUploadClient;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.learnforge.media.storage.IFileStorage;
import com.learnforge.media.storage.IMediaStorage;
import com.learnforge.media.storage.tencent.TencentFileStorage;
import com.learnforge.media.storage.tencent.TencentMediaStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties({TencentProperties.class})
public class TencentConfig {

    @Bean
    @ConditionalOnProperty(prefix = "tj.platform", name = "media", havingValue = "TENCENT")
    public VodClient tencentVodClient(TencentProperties properties){
        // 1. Authorization information
        Credential cred = new Credential(
                properties.getSecretId(), properties.getSecretKey());
        // 2. Configure timeout time
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setConnTimeout(1);
        httpProfile.setReadTimeout(10);
        httpProfile.setWriteTimeout(10);
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 2. Initialize client
        return new VodClient(cred, properties.getVod().getRegion());
    }

    @Bean
    @ConditionalOnProperty(prefix = "tj.platform", name = "media", havingValue = "TENCENT")
    public VodUploadClient tencentVodUploadClient(TencentProperties properties){
        // 1. Initialize client
        return new VodUploadClient(properties.getSecretId(), properties.getSecretKey());
    }

    @Bean
    @ConditionalOnProperty(prefix = "tj.platform", name = "media", havingValue = "TENCENT")
    public IMediaStorage tencentMediaStorage(VodClient tencentVodClient, TencentProperties properties){
        return new TencentMediaStorage(tencentVodClient, properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "tj.platform", name = "file", havingValue = "TENCENT")
    public COSClient tencentCosClient(TencentProperties properties){
        // 1. Authorization information
        COSCredentials cred = new BasicCOSCredentials(properties.getSecretId(), properties.getSecretKey());
        // 2. Basic configuration
        Region region = new Region(properties.getCos().getRegion());
        ClientConfig clientConfig = new ClientConfig(region);
        // 3. Initialize client
        return new COSClient(cred, clientConfig);
    }

    @Bean
    @ConditionalOnProperty(prefix = "tj.platform", name = "file", havingValue = "TENCENT")
    public TransferManager transferManager(COSClient tencentCosClient, TencentProperties properties){
        // Custom thread pool size, recommend setting to 16 or 32 when client and COS network is sufficient (e.g., using Tencent Cloud CVM in the same region for uploading to COS), which can fully utilize network resources
        // For public network transmission with poor network bandwidth quality, it is recommended to reduce this value to avoid request timeout due to slow internet speed.
        ExecutorService threadPool = Executors.newFixedThreadPool(4);

        // Pass in a threadPool, if not passed, the TransferManager will generate a single-threaded thread pool by default.
        TransferManager transferManager = new TransferManager(tencentCosClient, threadPool);

        // Set advanced interface configuration items
        // Chunk upload threshold and chunk size are 5MB and 1MB respectively
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(properties.getCos().getMultipartUploadThreshold());
        transferManagerConfiguration.setMinimumUploadPartSize(properties.getCos().getMinimumUploadPartSize());
        transferManager.setConfiguration(transferManagerConfiguration);

        return transferManager;
    }

    @Bean
    @ConditionalOnProperty(prefix = "tj.platform", name = "file", havingValue = "TENCENT")
    public IFileStorage tencentFileStorage(
            COSClient tencentCosClient, TransferManager transferManager, TencentProperties properties){
        return new TencentFileStorage(tencentCosClient, transferManager, properties);
    }
}
