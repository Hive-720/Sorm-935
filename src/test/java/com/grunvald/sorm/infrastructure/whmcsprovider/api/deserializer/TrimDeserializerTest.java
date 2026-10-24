package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.DeserializerTestSupport.deserializeFromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrimDeserializerTest {

    private final TrimDeserializer deserializer = new TrimDeserializer();

    @Test
    void value_isTrimmed() throws Exception {
        assertEquals("abc", deserializeFromString(deserializer, "  abc  "));
    }

    @Test
    void blankString_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, "   "));
        assertNull(deserializeFromString(deserializer, ""));
    }
}
