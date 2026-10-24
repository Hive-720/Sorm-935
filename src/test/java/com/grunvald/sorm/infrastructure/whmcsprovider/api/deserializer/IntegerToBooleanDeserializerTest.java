package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.DeserializerTestSupport.deserializeFromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IntegerToBooleanDeserializerTest {

    private final IntegerToBooleanDeserializer deserializer = new IntegerToBooleanDeserializer();

    @Test
    void one_isTrue() throws Exception {
        assertEquals(Boolean.TRUE, deserializeFromString(deserializer, "1"));
        assertEquals(Boolean.TRUE, deserializeFromString(deserializer, " 1 "));
    }

    @Test
    void zero_andAnyOtherNumber_isFalse() throws Exception {
        assertEquals(Boolean.FALSE, deserializeFromString(deserializer, "0"));
        assertEquals(Boolean.FALSE, deserializeFromString(deserializer, "2"));
        assertEquals(Boolean.FALSE, deserializeFromString(deserializer, "-1"));
    }

    @Test
    void blank_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, ""));
        assertNull(deserializeFromString(deserializer, "   "));
    }

    @Test
    void notANumber_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, "yes"));
    }
}
