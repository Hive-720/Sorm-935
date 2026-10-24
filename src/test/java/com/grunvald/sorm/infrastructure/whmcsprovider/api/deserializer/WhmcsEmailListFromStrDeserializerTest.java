package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.DeserializerTestSupport.deserializeFromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WhmcsEmailListFromStrDeserializerTest {

    private final WhmcsEmailListFromStrDeserializer deserializer = new WhmcsEmailListFromStrDeserializer();

    @Test
    void commaSeparated_extractedAndTrimmed() throws Exception {
        var result = deserializeFromString(deserializer, "a@b.com, John <john@c.com>,  d@e.com ");

        assertEquals(List.of("a@b.com", "john@c.com", "d@e.com"), result);
    }

    @Test
    void blankSegments_dropped() throws Exception {
        var result = deserializeFromString(deserializer, "a@b.com, ,  ,, c@d.com");

        assertEquals(List.of("a@b.com", "c@d.com"), result);
    }

    @Test
    void onlyBlanks_emptyList() throws Exception {
        // строка непустая, поэтому возвращается пустой список, а не null
        assertEquals(List.of(), deserializeFromString(deserializer, " , "));
    }

    @Test
    void blank_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, ""));
        assertNull(deserializeFromString(deserializer, "   "));
    }
}
