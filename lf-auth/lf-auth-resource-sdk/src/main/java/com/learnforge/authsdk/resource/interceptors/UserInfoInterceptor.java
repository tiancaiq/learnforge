package com.learnforge.authsdk.resource.interceptors;

import com.learnforge.auth.common.constants.JwtConstants;
import com.learnforge.common.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. Attempt to get user info from header
        String authorization = request.getHeader(JwtConstants.USER_HEADER);
        // 2. Check if empty
        if (authorization == null) {
            return true;
        }
        // 3. Convert to user id and save
        try {
            Long userId = Long.valueOf(authorization);
            UserContext.setUser(userId);
            return true;
        } catch (NumberFormatException e) {
            log.error("User identity info format is incorrect, {}, reason: {}", authorization, e.getMessage());
            return true;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Clear user info
        UserContext.removeUser();
    }
}
