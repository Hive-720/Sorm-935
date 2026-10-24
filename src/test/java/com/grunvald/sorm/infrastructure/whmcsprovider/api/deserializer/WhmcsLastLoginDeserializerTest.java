package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientsDetailsResponse;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.DeserializerTestSupport.deserializeFromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WhmcsLastLoginDeserializerTest {

    private final WhmcsLastLoginDeserializer deserializer = new WhmcsLastLoginDeserializer();

    @Test
    void validDateAndIp_parsed() throws Exception {
        var html = "Date: 15.01.2024 10:30<br>IP Address: 192.168.1.1<br>Hostname: host";

        var result = deserializeFromString(deserializer, html);

        assertEquals(OffsetDateTime.of(2024, 1, 15, 10, 30, 0, 0, ZoneOffset.UTC), result.getDate());
        assertEquals("192.168.1.1", result.getIpAddress());
    }

    @Test
    void invalidIp_omitted() throws Exception {
        var html = "Date: 15.01.2024 10:30<br>IP Address: not-an-ip";

        var result = deserializeFromString(deserializer, html);

        assertNull(result.getIpAddress());
        assertEquals(OffsetDateTime.of(2024, 1, 15, 10, 30, 0, 0, ZoneOffset.UTC), result.getDate());
    }

    @Test
    void unparseableDate_omitted() throws Exception {
        var html = "Date: 2024/01/15<br>IP Address: 192.168.1.1";

        var result = deserializeFromString(deserializer, html);

        assertNull(result.getDate());
        assertEquals("192.168.1.1", result.getIpAddress());
    }

    @Test
    void noMatch_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, "nothing useful here"));
    }

    @Test
    void blank_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, ""));
        assertNull(deserializeFromString(deserializer, "   "));
    }
}
