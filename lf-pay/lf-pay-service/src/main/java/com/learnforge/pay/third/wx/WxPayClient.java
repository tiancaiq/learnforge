package com.learnforge.pay.third.wx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.CommonException;
import com.learnforge.pay.sdk.constants.PayConstants;
import com.learnforge.pay.third.CommonPayProperties;
import com.learnforge.pay.third.wx.config.WxPayProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
@Component
public class WxPayClient {
    private final CloseableHttpClient wxPayClient;
    private final ObjectMapper objectMapper;
    private final CommonPayProperties payProperties;
    private final String appId;
    private final String mchId;

    public WxPayClient(
            CloseableHttpClient wxPayClient, WxPayProperties properties,
            ObjectMapper objectMapper, CommonPayProperties commonPayProperties) {
        this.wxPayClient = wxPayClient;
        this.objectMapper = objectMapper;
        this.appId = properties.getAppId();
        this.mchId = properties.getMchId();
        this.payProperties = commonPayProperties;
    }

    public String doPostJson(String requestPath, ObjectNode body) {
        JsonNode node = body.get("notify_url");
        try {
            // 2. Prepare Request
            HttpPost httpPost = new HttpPost(requestPath);
            httpPost.addHeader("Accept", MediaType.APPLICATION_JSON_VALUE);
            httpPost.addHeader("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(body), StandardCharsets.UTF_8));
            // 3. Send Request
            CloseableHttpResponse response = wxPayClient.execute(httpPost);
            // 4. Parse Response
            return EntityUtils.toString(response.getEntity());
        } catch (JsonProcessingException e) {
            throw new BadRequestException(0, "Request Parameter Format is Incorrect", e);
        } catch (IOException e) {
            log.error("WeChat Pay Request Processing Exception, Request Path: {}", requestPath);
            throw new CommonException("WeChat Pay Request Processing Exception", e);
        }
    }

    public String doGetJson(String requestPath, boolean withMch, NameValuePair... params) {
        try {
            // 1. Build Request Path
            URIBuilder uriBuilder = new URIBuilder(requestPath);
            // 2. Build Request Parameters
            if(withMch) {
                uriBuilder.addParameter("mchid", mchId);
            }
            if (params != null && params.length > 0) {
                uriBuilder.addParameters(Arrays.asList(params.clone()));
            }
            // 3. Send Request
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.addHeader("Accept", "application/json");
            CloseableHttpResponse response = wxPayClient.execute(httpGet);
            // 4. Parse Response
            return EntityUtils.toString(response.getEntity());
        } catch (URISyntaxException e) {
            log.error("WeChat Pay Request Path Exception, Request Path: {}", requestPath);
            throw new CommonException("WeChat Pay Request Path Exception", e);
        } catch (IOException e) {
            log.error("WeChat Pay Request Processing Exception, Request Path: {}", requestPath);
            throw new CommonException("WeChat Pay Request Processing Exception", e);
        }
    }

    public ObjectNode baseParam(boolean withApp, boolean withMch, Boolean isRefund) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if(withApp){
            objectNode.put("appid", appId);
        }
        if(withMch) {
                objectNode.put("mchid", mchId);
        }
        if(isRefund != null) {
            String notifyPath = isRefund ? "/notify/refund/" : "/notify/";
            objectNode.put("notify_url",
                    payProperties.getNotifyHost() + notifyPath + PayConstants.WX_CHANNEL_CODE);
        }
        return objectNode;
    }
}
