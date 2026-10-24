package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.DeserializerTestSupport.deserializeFromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IpAddressDeserializerTest {

    private final IpAddressDeserializer deserializer = new IpAddressDeserializer();

    @Test
    void validIp_returnedAsIs() throws Exception {
        assertEquals("192.168.1.1", deserializeFromString(deserializer, "192.168.1.1"));
    }

    @Test
    void invalidIp_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, "999.999.999.999"));
        assertNull(deserializeFromString(deserializer, "not an ip"));
    }

    @Test
    void blank_returnsNull() throws Exception {
        assertNull(deserializeFromString(deserializer, ""));
        assertNull(deserializeFromString(deserializer, "   "));
    }
}
