package com.grunvald.sorm.infrastructure.whmcsprovider.api;

import com.grunvald.sorm.service.whmcsprovider.ProviderException;

import java.util.Locale;


public class ErrorMapping {

    public static ProviderException.Error getErrorByMessage(String msg) {
        if (msg != null && msg.toLowerCase(Locale.ROOT).equals("client not found")) {
            return ProviderException.Error.NOT_FOUND;
        }

        return ProviderException.Error.EXTERNAL_ERROR;
    }
}