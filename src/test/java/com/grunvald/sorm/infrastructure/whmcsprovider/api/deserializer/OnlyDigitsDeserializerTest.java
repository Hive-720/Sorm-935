package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.DeserializerTestSupport.deserializeFromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OnlyDigitsDeserializerTest {

    private final OnlyDigitsDeserializer deserializer = new OnlyDigitsDeserializer();

    @Test
    void digitsKept_nonDigitsRemoved() throws Exception {
        assertEquals("79001234567", deserializeFromString(deserializer, "+7 (900) 123-45-67"));
        assertEquals("1234", deserializeFromString(deserializer, "1a2b3c4"));
    }

    @Test
    void onlyDigits_unchanged() throws Exception {
        assertEquals("12345", deserializeFromString(deserializer, "12345"));
    }

    @Test
    void noDigitsAtAll_returnsEmptyString() throws Exception {
        // replaceAll над непустой строкой даёт "", null не возвращается
        assertEquals("", deserializeFromString(deserializer, "abc"));
    }

    @Test
    void blank_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, ""));
        assertNull(deserializeFromString(deserializer, "   "));
    }
}
