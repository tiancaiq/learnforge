package com.learnforge.common.utils;

/**
 * @ClassName SignUtils
 * @author wusongsong
 * @since 2022/7/3 11:25
 * @version 1.0.0
 **/

import cn.hutool.core.util.HexUtil;
import com.learnforge.common.exceptions.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Signature utility class
 * <p>
 * Use accesskey and secretkey to sign and call third-party services
 */
@Slf4j
public class SignUtils {

    private static final String TOKEN_VERSION = "v1";

    private static final List<String> ALLOWED_METHODS = Arrays.asList("GET", "POST", "PUT", "DELETE", "HEAD");

    /**
     * Generate token for calling payment system modules or external systems
     * Signature (Signature) format before encryption: |v1-{AK}-{ExpireTime}|{SK}|{UrlPath}|{Method}|{QueryParam}|{body}|
     * Encrypted token format: v1-{AK}-{ExpireTime}-{Signature}
     *
     * @param urlPath Access URI
     * @param method Access method
     * @param queryParam Request parameters in the URL
     * @param body Request body
     * @param expireTime Expiration time in milliseconds
     * @param accessKey Encryption accesskey, public
     * @param secretKey Encryption string, confidential
     * @return Access token
     */
    public static String generateToken(String urlPath, String method, String queryParam,
                                       Object body, long expireTime, String accessKey, String secretKey) {
        log.debug("generateToken: urlPath={}, method={}, queryParam={}, body={}, expireTime={}", urlPath, method, queryParam, body, expireTime);
        if (StringUtils.isEmpty(accessKey) || StringUtils.isEmpty(secretKey)) {
            log.error("invalid AK or SK");
            throw new CommonException("Invalid AK or SK");
        }

        if (StringUtils.isEmpty(urlPath)) {
            log.error("urlPath can't be empty!");
            throw new CommonException("Empty url path");
        }

        if (!ALLOWED_METHODS.contains(method)) {
            log.error("{} isn't an allowed method", method);
            throw new CommonException("invalid request method");
        }

        String token = "";
        try {
            // |v1-{AK}-{ExpireTime}|{SK}|
            StringBuffer sbSign = new StringBuffer(String.format("|%s-%s-%d|%s|", TOKEN_VERSION,
                    accessKey, expireTime, secretKey));

            // {UrlPath}|
            sbSign.append(UriUtils.decode(urlPath, RequestUtils.UTF8_ENC)).append("|");

            // {Method}|
            sbSign.append(method.toUpperCase()).append("|");

            // {QueryParam}|
            sbSign.append(RequestUtils.toSortQueryParams(queryParam));
            sbSign.append("|");

            // {body}|
            if (!ObjectUtils.isEmpty(body)) {
                if (body instanceof String) {
                    sbSign.append(body);
                } else {
                    sbSign.append(JsonUtils.toJsonStr(body));
                }
            }
            sbSign.append("|");
            log.debug("signature contents: {}", sbSign.toString());

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(sbSign.toString().getBytes("UTF-8"));

            //  v2-{AK}-{ExpireTime}-{Signature}
            token = String.format("%s-%s-%s-%s", TOKEN_VERSION, accessKey, expireTime,
                    new String(HexUtil.encodeHex(md5.digest())));
            log.info("token contents is : {}", token);
        } catch (UnsupportedEncodingException e) {
            log.error("failed to decode url or query path");
            throw new RuntimeException("Bad encoded url path or query string");
        } catch (NoSuchAlgorithmException e) {
            log.error("");
        }

        return token;
    }

    /**
     * Validate token
     *
     * @param token Token from the request method
     * @param uri Request URI
     * @param method Request method
     * @param queryParam Request parameters
     * @param body Request body
     * @param accessKey Encryption accesskey, public
     * @param secretKey Encryption string, confidential
     * @return Validation result
     */
    public static boolean verifyToken(String token, String uri, String method,
                                      String queryParam, Object body, String accessKey, String secretKey) {
        log.debug("verifyToken: token={},urlPath={},method={},queryParam={},body={}", token, uri, method, queryParam, body);

        if (StringUtils.isEmpty(token)) {
            log.warn("null token");
            return false;
        }

        try {
            String[] tokenParts = token.split("-");

            if (tokenParts.length != 4) {
                log.warn("invalid token format");
                return false;
            }

            if (!TOKEN_VERSION.equals(tokenParts[0])) {
                log.warn("invalid token protocol version");
                return false;
            }

            long expireTime = Long.parseLong(tokenParts[2]);
            if (expireTime < System.currentTimeMillis()) {
                log.warn("expired token");
                return false;
            }

            if (token.equals(generateToken(uri, method, queryParam, body, expireTime, accessKey, secretKey))) {
                return true;
            }
        } catch (Exception e) {
            log.error("failed to parse token '{}',e:", token, e);

        }

        return false;
    }


    public static void main(String[] args) {
        String accessKey = "123";
        String secreatKey = "456";
        String urlPath = "/pay/123456789";
        String method = "GET";
        String params = "";
        Map<String, Object> body = new HashMap<>();
//        body.put("keyword", "123");
        String token = generateToken(urlPath, method, params, body, System.currentTimeMillis()  + 360000,
                accessKey, secreatKey);
        log.info("token : {}", token);

        boolean verifyToken = verifyToken(token, urlPath, method, params, body, accessKey, secreatKey);
        log.info("check {}", verifyToken);

    }

}
