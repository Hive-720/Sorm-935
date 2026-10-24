package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.DeserializerTestSupport.deserializeFromJson;
import static com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.DeserializerTestSupport.deserializeFromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CurrencyCodeToAlpha3DeserializerTest {

    private final CurrencyCodeToAlpha3Deserializer deserializer = new CurrencyCodeToAlpha3Deserializer();

    @Test
    void numericCode_returnsAlpha3() throws Exception {
        assertEquals("USD", deserializeFromString(deserializer, "840"));
        assertEquals("EUR", deserializeFromString(deserializer, "978"));
    }

    @Test
    void numericCodeWithSpaces_isTrimmed() throws Exception {
        assertEquals("USD", deserializeFromString(deserializer, " 840 "));
    }

    @Test
    void blank_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, ""));
        assertNull(deserializeFromString(deserializer, "   "));
    }

    @Test
    void notANumber_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, "abc"));
    }

    @Test
    void jsonNull_returnsNull() throws Exception {
        assertNull(deserializeFromJson(deserializer, "null"));
    }
}
