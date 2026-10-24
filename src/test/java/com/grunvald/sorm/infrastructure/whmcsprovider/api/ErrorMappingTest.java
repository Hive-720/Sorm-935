package com.grunvald.sorm.infrastructure.whmcsprovider.api;

import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.service.whmcsprovider.ProviderException.Error.EXTERNAL_ERROR;
import static com.grunvald.sorm.service.whmcsprovider.ProviderException.Error.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorMappingTest {

    @Test
    void getErrorByMessage_clientNotFound_anyCase_mapsToNotFound() {
        assertEquals(NOT_FOUND, ErrorMapping.getErrorByMessage("client not found"));
        assertEquals(NOT_FOUND, ErrorMapping.getErrorByMessage("CLIENT NOT FOUND"));
        assertEquals(NOT_FOUND, ErrorMapping.getErrorByMessage("Client Not Found"));
    }

    @Test
    void getErrorByMessage_otherMessage_mapsToExternalError() {
        assertEquals(EXTERNAL_ERROR, ErrorMapping.getErrorByMessage("invalid api command"));
        assertEquals(EXTERNAL_ERROR, ErrorMapping.getErrorByMessage(""));
    }

    @Test
    void getErrorByMessage_null_mapsToExternalError() {
        assertEquals(EXTERNAL_ERROR, ErrorMapping.getErrorByMessage(null));
    }
}
