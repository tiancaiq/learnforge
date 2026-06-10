package com.learnforge.media.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tj.tencent")
public class TencentProperties {
    private Long appId;
    private String secretId;
    private String secretKey;
    private VodProperties vod;
    private CosProperties cos;
    @Data
    public static class VodProperties{
        /*Whether to enable Tencent VOD*/
        private boolean enable;
        /*Signature validity period*/
        private long vodValidSeconds;
        /*Region*/
        private String region;
        /*Task flow*/
        private String procedure;
        /*Anti-leeching secret key*/
        private String urlKey;
        /*Player configuration*/
        private String pfcg;
    }
    @Data
    public static class CosProperties{
        /*Region*/
        private String region;
        /*Storage bucket*/
        private String bucket;
        /*Trigger chunk upload threshold*/
        private long multipartUploadThreshold;
        /*Minimum chunk size for chunk upload*/
        private long minimumUploadPartSize;
    }
}
