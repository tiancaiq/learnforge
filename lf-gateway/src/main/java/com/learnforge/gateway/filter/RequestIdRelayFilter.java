package com.learnforge.gateway.filter;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.learnforge.common.constants.Constant.*;

@Slf4j
@Component
public class RequestIdRelayFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. Generate RequestId
        String requestId = UUID.randomUUID().toString(true);
        // 2. Save to log variable pool
        MDC.put(REQUEST_ID_HEADER, requestId);
        // 3. Update request header, add identifier
        String path = exchange.getRequest().getPath().toString();
        exchange = exchange.mutate().request(b -> {
                    // 3.1. Add request id identifier
                    b.header(REQUEST_ID_HEADER, requestId);
                    // 3.2. Add gateway identifier
                    if (!path.startsWith("/ps/notify")) {
                        b.header(REQUEST_FROM_HEADER, GATEWAY_ORIGIN_NAME);
                    }
                }
        ).build();

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
