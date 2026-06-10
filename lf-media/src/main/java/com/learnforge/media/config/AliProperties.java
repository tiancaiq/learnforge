package com.learnforge.media.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tj.ali")
public class AliProperties {
    private String accessId;
    private String accessKey;

    private OssProperties oos;

    @Data
    public static class OssProperties {
        /*Region*/
        private String region;
        /*Domain*/
        private String endpoint;
        /*Bucket name*/
        private String bucket;
    }
}
