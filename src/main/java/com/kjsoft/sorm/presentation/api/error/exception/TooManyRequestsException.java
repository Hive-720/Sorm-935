package com.kjsoft.sorm.presentation.api.error.exception;


public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException(String msg) {
        super(msg);
    }
}