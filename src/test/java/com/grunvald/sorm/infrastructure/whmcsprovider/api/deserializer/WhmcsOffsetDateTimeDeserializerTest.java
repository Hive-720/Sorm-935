package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.DeserializerTestSupport.deserializeFromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WhmcsOffsetDateTimeDeserializerTest {

    private final WhmcsOffsetDateTimeDeserializer deserializer = new WhmcsOffsetDateTimeDeserializer();

    @Test
    void dateTime_parsedAsUtc() throws Exception {
        var result = deserializeFromString(deserializer, "2024-01-02 03:04:05");

        assertEquals(OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC), result);
    }

    @Test
    void dateOnly_parsedAsStartOfDayUtc() throws Exception {
        var result = deserializeFromString(deserializer, "2024-01-02");

        assertEquals(OffsetDateTime.of(2024, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC), result);
    }

    @Test
    void unparseableFormat_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, "02/01/2024"));
        assertNull(deserializeFromString(deserializer, "garbage"));
    }

    @Test
    void empty_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, ""));
    }
}
