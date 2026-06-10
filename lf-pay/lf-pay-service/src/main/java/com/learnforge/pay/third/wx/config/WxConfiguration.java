package com.learnforge.pay.third.wx.config;

import cn.hutool.crypto.PemUtil;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

@Configuration
@EnableConfigurationProperties(WxPayProperties.class)
public class WxConfiguration {

    /**
     * WeChat Pay Certificate Manager
     */
    @Bean(destroyMethod = "stop")
    public CertificatesManager certificatesManager(WxPayProperties properties)
            throws HttpCodeException, GeneralSecurityException, IOException {
        // 1. Load Private Key
        PrivateKey privateKey = PemUtil.readPemPrivateKey(
                new ByteArrayInputStream(properties.getPrivateKey().getBytes(StandardCharsets.UTF_8)));
        // 2. Signature Tool
        PrivateKeySigner privateKeySigner = new PrivateKeySigner(properties.getMchSerialNo(), privateKey);
        WechatPay2Credentials wechatPay2Credentials = new WechatPay2Credentials(properties.getMchId(), privateKeySigner);
        // 3. Platform Certificate Manager
        CertificatesManager certificatesManager = CertificatesManager.getInstance();
        certificatesManager.putMerchant(
                properties.getMchId(), wechatPay2Credentials, properties.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        return certificatesManager;
    }

    @Bean
    public CloseableHttpClient closeableHttpClient(WxPayProperties properties, CertificatesManager certificatesManager)
            throws NotFoundException {
        // 1. Load Private Key
        PrivateKey privateKey = PemUtil.readPemPrivateKey(
                new ByteArrayInputStream(properties.getPrivateKey().getBytes(StandardCharsets.UTF_8)));
        // 2. Initialize
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                .withMerchant(properties.getMchId(), properties.getMchSerialNo(), privateKey)
                .withValidator(new WechatPay2Validator(certificatesManager.getVerifier(properties.getMchId())))
                ;

        // 3. Build
        return builder.build();
    }
}
