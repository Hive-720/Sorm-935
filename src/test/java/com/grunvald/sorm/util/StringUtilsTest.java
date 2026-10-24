package com.grunvald.sorm.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void concatStrings_bothNull_returnsNull() {
        assertNull(StringUtils.concatStrings(null, null, " "));
    }

    @Test
    void concatStrings_bothEmpty_returnsNull() {
        assertNull(StringUtils.concatStrings("", "", "-"));
    }

    @Test
    void concatStrings_firstEmpty_returnsSecond() {
        assertEquals("b", StringUtils.concatStrings(null, "b", "-"));
        assertEquals("b", StringUtils.concatStrings("", "b", "-"));
    }

    @Test
    void concatStrings_secondEmpty_returnsFirst() {
        assertEquals("a", StringUtils.concatStrings("a", null, "-"));
        assertEquals("a", StringUtils.concatStrings("a", "", "-"));
    }

    @Test
    void concatStrings_bothPresent_joinsWithSeparator() {
        assertEquals("a-b", StringUtils.concatStrings("a", "b", "-"));
    }

    @Test
    void concatStrings_nullSeparator_joinsWithoutSeparator() {
        assertEquals("ab", StringUtils.concatStrings("a", "b", null));
    }
}
