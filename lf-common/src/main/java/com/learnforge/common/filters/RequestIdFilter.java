package com.learnforge.common.filters;


import com.learnforge.common.constants.Constant;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter(filterName = "requestIdFilter", urlPatterns = "/**")
public class RequestIdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 1. Get request
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // 2. Get requestId from request header
        String requestId = request.getHeader(Constant.REQUEST_ID_HEADER);
        try {
            // 3. Store in MDC
            MDC.put(Constant.REQUEST_ID_HEADER, requestId);
            filterChain.doFilter(request, servletResponse);
        }finally {
            // 4. Remove
            MDC.clear();
        }
    }
}
