package com.grunvald.sorm.presentation.graphqlapi.error;


public class BadRequestException extends RuntimeException {

    public BadRequestException(String msg) {
        super(msg);
    }
}