package com.learnforge.gateway.filter;

import com.learnforge.authsdk.gateway.util.AuthUtil;
import com.learnforge.common.domain.R;
import com.learnforge.common.domain.dto.LoginUserDTO;
import com.learnforge.gateway.config.AuthProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.learnforge.auth.common.constants.JwtConstants.AUTHORIZATION_HEADER;
import static com.learnforge.auth.common.constants.JwtConstants.USER_HEADER;

@Component
public class AccountAuthFilter implements GlobalFilter, Ordered {

    private final AuthUtil authUtil;
    private final AuthProperties authProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public AccountAuthFilter(AuthUtil authUtil, AuthProperties authProperties) {
        this.authUtil = authUtil;
        this.authProperties = authProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. Get request request information
        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethodValue();
        String path = request.getPath().toString();
        String antPath = method + ":" + path;

        // 2. Determine if it is a path that does not require login
        if(isExcludePath(antPath)){
            // Directly proceed
            return chain.filter(exchange);
        }

        // 3. Try to get user information
        List<String> authHeaders = exchange.getRequest().getHeaders().get(AUTHORIZATION_HEADER);
        String token = authHeaders == null ? "" : authHeaders.get(0);
        R<LoginUserDTO> r = authUtil.parseToken(token);

        // 4. If user is logged in, try to update request header to pass user information
        if(r.success()){
            exchange.mutate()
                    .request(builder -> builder.header(USER_HEADER, r.getData().getUserId().toString()))
                    .build();
        }

        // 5. Check permission
        authUtil.checkAuth(antPath, r);

        // 6. Proceed
        return chain.filter(exchange);
    }

    private boolean isExcludePath(String antPath) {
        for (String pathPattern : authProperties.getExcludePath()) {
            if(antPathMatcher.match(pathPattern, antPath)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 1000;
    }
}
