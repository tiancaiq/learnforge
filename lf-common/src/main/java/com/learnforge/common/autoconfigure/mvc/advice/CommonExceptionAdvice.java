package com.learnforge.common.autoconfigure.mvc.advice;

import com.learnforge.common.constants.Constant;
import com.learnforge.common.domain.R;
import com.learnforge.common.exceptions.CommonException;
import com.learnforge.common.exceptions.DbException;
import com.learnforge.common.utils.WebUtils;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.NestedServletException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class CommonExceptionAdvice {

    @ExceptionHandler(DbException.class)
    public Object handleDbException(DbException e) {
        log.error("mysql database operation exception -> ", e);
        return processResponse(e.getStatus(), e.getCode(), e.getMessage());
    }

    @ExceptionHandler(CommonException.class)
    public Object handleBadRequestException(CommonException e) {
        log.error("Custom exception -> {}, status code: {}, exception cause: {}  ",e.getClass().getName(), e.getStatus(), e.getMessage());
        log.debug("", e);
        return processResponse(e.getStatus(), e.getCode(), e.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public Object handleFeignException(FeignException e) {
        log.error("feign remote call exception -> ", e);
        return processResponse(e.status(), e.status(), e.contentUTF8());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getAllErrors()
                .stream().map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("|"));
        log.error("Request parameter validation exception -> {}", msg);
        log.debug("", e);
        return processResponse(400, 400, msg);
    }
    @ExceptionHandler(BindException.class)
    public Object handleBindException(BindException e) {
        log.error("Request parameter binding exception -> BindException, {}", e.getMessage());
        log.debug("", e);
        return processResponse(400, 400, "Request parameter format error");
    }

    @ExceptionHandler(NestedServletException.class)
    public Object handleNestedServletException(NestedServletException e) {
        log.error("Parameter exception -> NestedServletException, {}", e.getMessage());
        log.debug("", e);
        return processResponse(400, 400, "Request parameter exception");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Object handViolationException(ConstraintViolationException e) {
        log.error("Request parameter exception -> ConstraintViolationException, {}", e.getMessage());

        return processResponse( HttpStatus.OK.value(), HttpStatus.BAD_REQUEST.value(),
                e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).distinct().collect(Collectors.joining("|"))
                );
    }

    @ExceptionHandler(Exception.class)
    public Object handleRuntimeException(Exception e) {
        log.error("Other exception uri: {} -> ", WebUtils.getRequest().getRequestURI(), e);
        return processResponse(500, 500, "Server internal exception");
    }

    private Object processResponse(int status, int code, String msg){
        // 1. Mark response exception as handled (to avoid duplicate processing)
        WebUtils.setResponseHeader(Constant.BODY_PROCESSED_MARK_HEADER, "true");
        // 2. If it's a gateway request, change HTTP status code to 200 for return, frontend judges status based on business status code code
        // If it's a microservice request, HTTP status code is returned as original, microservice handles fallback itself
        return WebUtils.isGatewayRequest() ?
                R.error(code, msg).requestId(MDC.get(Constant.REQUEST_ID_HEADER))
                : ResponseEntity.status(status).body(msg);
    }
}
