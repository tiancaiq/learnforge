package com.learnforge.pay.third.wx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tj.pay.wx")
public class WxPayProperties{
    /**
     * appId
     */
    private String appId;
    /**
     * Merchant ID
     */
    private String mchId;
    /**
     * Merchant Certificate Serial Number
     */
    private String mchSerialNo;
    /**
     * Private Key String
     */
    private String privateKey;
    /**
     * APIv3 Secret Key
     */
    private String apiV3Key;
}
