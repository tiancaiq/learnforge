package com.learnforge.common.utils;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
@Data
@Accessors(chain = true, fluent = true)
public class CookieBuilder {
    private Charset charset = StandardCharsets.UTF_8;
    private int maxAge = -1;
    private String path = "/";
    private boolean httpOnly;
    private String name;
    private String value;
    private String domain;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public CookieBuilder(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * Build cookie, encode cookie value with UTF-8 to avoid Chinese encoding issues
     */
    public void build(){
        if (response == null) {
            log.error("Response is null, cannot write cookie");
            return;
        }
        Cookie cookie = new Cookie(name, URLEncoder.encode(value, charset));
        if(StringUtils.isNotBlank(domain)) {
            cookie.setDomain(domain);
        }else if (request != null) {
            String serverName = request.getServerName();
            serverName = StringUtils.subAfter(serverName, ".", false);
            cookie.setDomain("." + serverName);
        }
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        log.debug("Generate cookie, encoding method: {}, [{}={}, domain: {}; maxAge={}; path={}; httpOnly={}]",
                charset.name(), name, value, domain, maxAge, path, httpOnly);
        response.addCookie(cookie);
    }

    /**
     * Decode cookie value with UTF-8 to avoid Chinese encoding issues
     * @param cookieValue Original cookie value
     * @return Decoded value
     */
    public String decode(String cookieValue){
        return URLDecoder.decode(cookieValue, charset);
    }
}
