package com.learnforge.common.utils;


import com.learnforge.common.constants.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

@Slf4j
public class WebUtils {

    /**
     * Get ServletRequestAttributes
     *
     * @return ServletRequestAttributes
     */
    public static ServletRequestAttributes getServletRequestAttributes() {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        if (ra == null) {
            return null;
        }
        return (ServletRequestAttributes) ra;
    }

    /**
     * Get request
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = getServletRequestAttributes();
        return servletRequestAttributes == null ? null : servletRequestAttributes.getRequest();
    }

    /**
     * Get response
     *
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes = getServletRequestAttributes();
        return servletRequestAttributes == null ? null : servletRequestAttributes.getResponse();
    }

    /**
     * Get content from request header
     *
     * @param headerName request header name
     * @return request header value
     */
    public static String getHeader(String headerName) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return getRequest().getHeader(headerName);
    }

    public static void setResponseHeader(String key, String value){
        HttpServletResponse response = getResponse();
        if (response == null) {
            return;
        }
        response.setHeader(key, value);
    }


    public static String getRequestId() {
        return getHeader(Constant.REQUEST_ID_HEADER);
    }

    public static boolean isGatewayRequest() {
        String originName = getHeader(Constant.REQUEST_FROM_HEADER);
        return Constant.GATEWAY_ORIGIN_NAME.equals(originName);
    }

    public static boolean isFeignRequest() {
        String originName = getHeader(Constant.REQUEST_FROM_HEADER);
        return Constant.FEIGN_ORIGIN_NAME.equals(originName);
    }

    public static boolean isSuccess() {
        HttpServletResponse response = getResponse();
        return response != null && response.getStatus() < 300;
    }

    /**
     * Assemble request parameters in request url into key1=value1&key2=value2
     * If key corresponds to multiple values, separate them with commas, for example key1 corresponds to value1, key2 corresponds to value2, value3, key1=value1&key2=value2,value3
     *
     * @param request
     * @return return concatenated string
     */
    public static String getParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        return getParameters(parameterMap);
    }

    /**
     * Assemble request parameters in request url into key1=value1&key2=value2
     * If key corresponds to multiple values, separate them with commas, for example key1 corresponds to value1, key2 corresponds to value2, value3, key1=value1&key2=value2,value3
     *
     * @param queries
     * @return
     */
    public  static <T> String getParameters(final Map<String, T> queries) {
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<String, T> entry : queries.entrySet()) {
            if(entry.getValue() instanceof String[]){
                buffer.append(entry.getKey()).append(String.join(",", ((String[])entry.getValue())))
                    .append("&");
            }else if(entry.getValue() instanceof Collection){
                buffer.append(entry.getKey()).append(
                        CollUtils.join(((Collection<String>)entry.getValue()),",")
                ).append("&");
            }
        }
        return buffer.length() > 0 ? buffer.substring(0, buffer.length() - 1) : StringUtils.EMPTY;
    }

    /**
     * Get uri from request url
     *
     * @param url
     * @return
     */
    public static String getUri(String url){
        if(StringUtils.isEmpty(url)) {
            return null;
        }

        String uri = url;
        //Remove http:// or https from uri
        if(uri.contains("http://") ){
            uri = uri.replace("http://", StringUtils.EMPTY);
        }else if(uri.contains("https://")){
            uri = uri.replace("https://", StringUtils.EMPTY);
        }

        int endIndex = uri.length(); //The position of the last character in uri plus one
        if(uri.contains("?")){
            endIndex = uri.indexOf("?");
        }
        return uri.substring(uri.indexOf("/"), endIndex);
    }

    public static String getRemoteAddr() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "";
        }
        return request.getRemoteAddr();
    }

    public static CookieBuilder cookieBuilder(){
        return new CookieBuilder(getRequest(), getResponse());
    }
}
