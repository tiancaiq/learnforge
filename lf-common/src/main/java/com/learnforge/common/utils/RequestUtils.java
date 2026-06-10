package com.learnforge.common.utils;

import org.springframework.web.util.UriUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequestUtils {
    public static final String UTF8_ENC = "UTF-8";


    /**
     * Sort request parameters in ascending order and reassemble
     *
     * @param originQueryParam Original request parameters
     * @return Reassembled parameters
     */
    public static String toSortQueryParams(String originQueryParam) {
        List<String> queryParams = new ArrayList<String>();

        //Assemble and decode
        for (String kv : originQueryParam.split("&")) {
            String[] t = kv.split("=");
            if (t.length > 1) {
                queryParams.add(String.format("%s=%s", UriUtils.decode(t[0], UTF8_ENC), UriUtils.decode(t[1], UTF8_ENC)));
            } else {
                queryParams.add(String.format("%s=", UriUtils.decode(t[0], UTF8_ENC)));
            }
        }
        //Sort Order
        Collections.sort(queryParams);
        StringBuffer buffer = new StringBuffer();
        //Reassemble
        for (String queryParm : queryParams) {
            buffer.append(queryParm).append("&");
        }

        return buffer.length() > 0 ? buffer.substring(0, buffer.length() - 2) : StringUtils.EMPTY;
    }
}
