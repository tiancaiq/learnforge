package com.learnforge.gateway.exception.handler;

import com.learnforge.common.constants.Constant;
import com.learnforge.common.domain.R;
import com.learnforge.common.exceptions.CommonException;
import com.learnforge.common.exceptions.UnauthorizedException;
import com.learnforge.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.learnforge.common.constants.ErrorInfo.Code.FAILED;
import static com.learnforge.common.constants.ErrorInfo.Msg.SERVER_INTER_ERROR;

@Slf4j
@Component
public class GatewayExceptionHandler implements ErrorWebExceptionHandler, Ordered {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // 1. Get response
        ServerHttpResponse response = exchange.getResponse();
        // 2. Determine if already processed
        if (response.isCommitted()) {
            // If already submitted, directly end to avoid duplicate processing
            return Mono.error(ex);
        }

        // 3. Translate exceptions by type, translation result is easy for frontend to understand
        String message;
        int code = FAILED;
        if (ex instanceof UnauthorizedException) {
            // Login exception, directly return status code
            UnauthorizedException e = (UnauthorizedException) ex;
            return Mono.error(new ResponseStatusException(e.getStatus(), e.getMessage(), e));
        } else if (ex instanceof CommonException) {
            CommonException e = (CommonException) ex;
            code = e.getCode();
            message = e.getMessage();
        } else if (ex instanceof NotFoundException) {
            message = "Service not exists";
        } else if (ex instanceof ResponseStatusException) {
            message = ex.getMessage();
        } else {
            message = SERVER_INTER_ERROR;
            // 4. Record log
            writeLog(exchange, ex);
        }
        // 5. Set response result as JSON
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        // 6. Package response result and write out
        R<Object> r = R.error(code, message);
        List<String> requestIds = response.getHeaders().get(Constant.REQUEST_ID_HEADER);
        if (requestIds != null) {
            r.requestId(requestIds.get(0));
        }
        byte[] resp = JsonUtils.toJsonStr(r).getBytes(StandardCharsets.UTF_8);
        return response.writeWith(
                Mono.fromSupplier(
                        () -> response.bufferFactory().wrap(resp)
                ));
    }

    private void writeLog(ServerWebExchange exchange, Throwable ex) {
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        String host = uri.getHost();
        int port = uri.getPort();
        log.error("Gateway route exception - host: {}, port: {}, uri: {}, errormessage:",
                host, port, request.getPath(), ex);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}