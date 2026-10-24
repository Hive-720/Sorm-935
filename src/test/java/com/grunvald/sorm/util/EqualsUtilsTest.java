package com.grunvald.sorm.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EqualsUtilsTest {

    @Test
    void isBigDecimalEqual_bothNull_true() {
        assertTrue(EqualsUtils.isBigDecimalEqual(null, null));
    }

    @Test
    void isBigDecimalEqual_oneNull_false() {
        assertFalse(EqualsUtils.isBigDecimalEqual(null, BigDecimal.ONE));
        assertFalse(EqualsUtils.isBigDecimalEqual(BigDecimal.ONE, null));
    }

    @Test
    void isBigDecimalEqual_sameScale_true() {
        assertTrue(EqualsUtils.isBigDecimalEqual(new BigDecimal("1.10"), new BigDecimal("1.10")));
    }

    @Test
    void isBigDecimalEqual_differentScaleSameValue_true() {
        // compareTo, а не equals: 1.0 == 1.00
        assertTrue(EqualsUtils.isBigDecimalEqual(new BigDecimal("1.0"), new BigDecimal("1.00")));
    }

    @Test
    void isBigDecimalEqual_differentValue_false() {
        assertFalse(EqualsUtils.isBigDecimalEqual(new BigDecimal("1.0"), new BigDecimal("1.1")));
    }

    @Test
    void isOffsetDateTimeEqual_bothNull_true() {
        assertTrue(EqualsUtils.isOffsetDateTimeEqual(null, null));
    }

    @Test
    void isOffsetDateTimeEqual_oneNull_false() {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        assertFalse(EqualsUtils.isOffsetDateTimeEqual(null, now));
        assertFalse(EqualsUtils.isOffsetDateTimeEqual(now, null));
    }

    @Test
    void isOffsetDateTimeEqual_sameInstantDifferentOffset_true() {
        var utc = OffsetDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        var plusTwo = utc.withOffsetSameInstant(ZoneOffset.ofHours(2));

        assertTrue(EqualsUtils.isOffsetDateTimeEqual(utc, plusTwo));
    }

    @Test
    void isOffsetDateTimeEqual_differentInstant_false() {
        var a = OffsetDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        var b = a.plusSeconds(1);

        assertFalse(EqualsUtils.isOffsetDateTimeEqual(a, b));
    }

    @Test
    void areListsEqual_bothNull_true() {
        assertTrue(EqualsUtils.areListsEqual(null, null));
    }

    @Test
    void areListsEqual_oneNull_false() {
        assertFalse(EqualsUtils.areListsEqual(null, List.of("a")));
        assertFalse(EqualsUtils.areListsEqual(List.of("a"), null));
    }

    @Test
    void areListsEqual_differentSize_false() {
        assertFalse(EqualsUtils.areListsEqual(List.of("a"), List.of("a", "b")));
    }

    @Test
    void areListsEqual_sameElementsDifferentOrder_true() {
        assertTrue(EqualsUtils.areListsEqual(List.of("a", "b", "c"), List.of("c", "b", "a")));
    }

    @Test
    void areListsEqual_differentElements_false() {
        assertFalse(EqualsUtils.areListsEqual(List.of("a", "b"), List.of("a", "x")));
    }

    @Test
    void areListsEqual_duplicatesAccountedBySizeCheck() {
        // сначала сверяются размеры, поэтому дубликаты делают списки неравными,
        // даже если множества элементов совпадают
        assertFalse(EqualsUtils.areListsEqual(List.of("a", "a"), List.of("a")));
        assertTrue(EqualsUtils.areListsEqual(List.of("a", "b", "a"), List.of("b", "a", "a")));
    }
}
