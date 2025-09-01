package com.kjsoft.sorm.presentation.graphqlapi.error;


public class BadRequestException extends RuntimeException {

    public BadRequestException(String msg) {
        super(msg);
    }
}