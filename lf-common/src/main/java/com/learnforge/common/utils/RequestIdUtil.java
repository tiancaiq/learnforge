package com.learnforge.common.utils;

import cn.hutool.core.lang.UUID;
import org.slf4j.MDC;

import static com.learnforge.common.constants.Constant.REQUEST_ID_HEADER;


public class RequestIdUtil {
    public static void markRequest() {
        // 1. Check if it already exists
        String requestId = MDC.get(REQUEST_ID_HEADER);
        if(requestId != null){
            return;
        }
        // 2. Try to get from request header
        requestId = WebUtils.getRequestId();
        // 3. Check again
        if (requestId == null) {
            // Generate a new requestId directly if it does not exist
            requestId = UUID.randomUUID().toString(true);
        }
        // 4. Save
        MDC.put(REQUEST_ID_HEADER, requestId);
    }
    public static void clear(){
        MDC.clear();
    }
}
