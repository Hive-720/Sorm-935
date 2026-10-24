package com.grunvald.sorm.util;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void now_returnsUtcOffset() {
        OffsetDateTime now = new DateUtils().now();

        assertEquals(ZoneOffset.UTC, now.getOffset());
    }

    @Test
    void now_isCloseToCurrentTime() {
        OffsetDateTime before = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime now = new DateUtils().now();
        OffsetDateTime after = OffsetDateTime.now(ZoneOffset.UTC);

        assertFalse(now.isBefore(before), "now() не может быть раньше момента до вызова");
        assertFalse(now.isAfter(after), "now() не может быть позже момента после вызова");
    }
}
