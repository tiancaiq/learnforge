package com.learnforge.authsdk.resource.interceptors;

import com.learnforge.common.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. Attempt to get user info
        Long userId = UserContext.getUser();
        // 2. Check if logged in
        if (userId == null) {
            response.setStatus(401);
            response.sendError(401, "Unauthenticated users cannot access!");
            // 2.3. Unauthenticated, directly intercept
            return false;
        }
        // 3. Allow access if logged in
        return true;
    }
}
