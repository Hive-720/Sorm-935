package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.DeserializerTestSupport.deserializeFromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WhmcsEmailFromStrDeserializerTest {

    private final WhmcsEmailFromStrDeserializer deserializer = new WhmcsEmailFromStrDeserializer();

    @Test
    void nameInBrackets_extractsAddress() throws Exception {
        assertEquals("john@example.com", deserializeFromString(deserializer, "John Doe <john@example.com>"));
    }

    @Test
    void plainAddress_returnedTrimmed() throws Exception {
        assertEquals("john@example.com", deserializeFromString(deserializer, "  john@example.com  "));
    }

    @Test
    void multipleBrackets_usesLastPair() throws Exception {
        // lastIndexOf('<') берёт последнюю открывающую скобку
        assertEquals("real@example.com", deserializeFromString(deserializer, "<fake> <real@example.com>"));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"   "})
    void blank_returnsNull(String value) throws Exception {
        assertNull(deserializeFromString(deserializer, value));
    }

    @Test
    void staticCleanEmail_matchesDeserializerBehavior() {
        assertEquals("a@b.c", WhmcsEmailFromStrDeserializer.cleanEmail("A <a@b.c>"));
        assertNull(WhmcsEmailFromStrDeserializer.cleanEmail("   "));
    }
}
