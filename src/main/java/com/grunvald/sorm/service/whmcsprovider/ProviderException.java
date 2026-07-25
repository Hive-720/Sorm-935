package com.grunvald.sorm.service.whmcsprovider;

import lombok.Getter;


@Getter
public class ProviderException extends RuntimeException {
    private final Error error;


    public ProviderException(Error error, String msg) {
        super(msg);
        this.error = error;
    }

    public boolean isNotFoundError() {
        return error == Error.NOT_FOUND;
    }

    public enum Error {
        NOT_FOUND,
        EXTERNAL_ERROR
    }
}