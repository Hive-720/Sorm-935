package com.grunvald.sorm.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class IpAddressUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {"127.0.0.1", "192.168.0.1", "0.0.0.0", "255.255.255.255", "0:0:0:0:0:0:0:1", "2001:db8:0:0:0:0:0:1"})
    void isValidIp_validIps_returnsTrue(String ip) {
        assertTrue(IpAddressUtil.isValidIp(ip));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "1.2.3", "1.2.3.4.5", "256.1.1.1", "-1.2.3.4", "1.2.3.4.", ""})
    void isValidIp_invalidIps_returnsFalse(String ip) {
        assertFalse(IpAddressUtil.isValidIp(ip));
    }

    @Test
    void isValidIp_null_returnsFalse() {
        assertFalse(IpAddressUtil.isValidIp(null));
    }

    @Test
    void isValidIp_compressedIpv6_returnsFalse() {
        // InetAddress.getHostAddress() возвращает развёрнутую форму, поэтому
        // сжатая запись ("::1", "2001:db8::1") не совпадает со входом и бракуется
        assertFalse(IpAddressUtil.isValidIp("::1"));
        assertFalse(IpAddressUtil.isValidIp("2001:db8::1"));
    }
}
