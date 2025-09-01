package com.kjsoft.sorm.service.exception;


public class InternalErrorException extends RuntimeException {

    public InternalErrorException(String message, Throwable e) {
        super(message, e);
    }
}