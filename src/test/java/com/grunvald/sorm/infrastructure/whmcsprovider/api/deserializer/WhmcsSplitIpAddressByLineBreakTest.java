package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.DeserializerTestSupport.deserializeFromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WhmcsSplitIpAddressByLineBreakTest {

    private final WhmcsSplitIpAddressByLineBreak deserializer = new WhmcsSplitIpAddressByLineBreak();

    @Test
    void lineBreakSeparated_validIpsCollected() throws Exception {
        var result = deserializeFromString(deserializer, "192.168.1.1\n10.0.0.1\n172.16.0.1");

        assertEquals(List.of("192.168.1.1", "10.0.0.1", "172.16.0.1"), result);
    }

    @Test
    void invalidIps_filteredOut() throws Exception {
        var result = deserializeFromString(deserializer, "192.168.1.1\nnot-an-ip\n10.0.0.1\n999.1.1.1");

        assertEquals(List.of("192.168.1.1", "10.0.0.1"), result);
    }

    @Test
    void allInvalid_emptyList() throws Exception {
        assertEquals(List.of(), deserializeFromString(deserializer, "aaa\nbbb"));
    }

    @Test
    void blank_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, ""));
        assertNull(deserializeFromString(deserializer, "   "));
    }
}
