package com.learnforge.common.exceptions;

/**
 * Database exception
 **/
public class DbException extends CommonException{
    public DbException(String message) {
        super(message);
    }

    public DbException(int code, String message) {
        super(code, message);
    }

    public DbException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
