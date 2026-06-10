package com.learnforge.common.exceptions;

/**
 * Business illegal exception
 **/
public class BizIllegalException extends CommonException{
    public BizIllegalException(String message) {
        super(message);
    }

    public BizIllegalException(int code, String message) {
        super(code, message);
    }

    public BizIllegalException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
