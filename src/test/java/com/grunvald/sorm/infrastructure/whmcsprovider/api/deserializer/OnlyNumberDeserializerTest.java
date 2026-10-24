package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.DeserializerTestSupport.deserializeFromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OnlyNumberDeserializerTest {

    private final OnlyNumberDeserializer deserializer = new OnlyNumberDeserializer();

    @Test
    void plainNumber_parsed() throws Exception {
        assertEquals(new BigDecimal("123.45"), deserializeFromString(deserializer, "123.45"));
    }

    @Test
    void numberWithGarbage_cleanedToNumber() throws Exception {
        assertEquals(new BigDecimal("10.50"), deserializeFromString(deserializer, "$10.50"));
        assertEquals(new BigDecimal("123"), deserializeFromString(deserializer, "1a2b3"));
    }

    @Test
    void negativeNumber_signIsStripped() throws Exception {
        // минус отбрасывается как не-цифра: -1.5 превращается в 1.5
        assertEquals(new BigDecimal("1.5"), deserializeFromString(deserializer, "-1.5"));
    }

    @Test
    void multipleDots_unparseable_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, "1.2.3"));
    }

    @Test
    void noDigits_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, "abc"));
    }

    @Test
    void blank_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, ""));
        assertNull(deserializeFromString(deserializer, "   "));
    }
}
