package com.grunvald.sorm.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyUtilTest {

    @Test
    void getValidCurrency_validAlpha3_returnsNormalizedCode() {
        assertEquals("USD", CurrencyUtil.getValidCurrency("USD"));
    }

    @Test
    void getValidCurrency_lowercaseAlpha3_returnsCode() {
        // Currency.getInstance чувствителен к регистру, "usd" невалиден
        assertNull(CurrencyUtil.getValidCurrency("usd"));
    }

    @Test
    void getValidCurrency_unknownCode_returnsNull() {
        assertNull(CurrencyUtil.getValidCurrency("XXX_UNKNOWN"));
    }

    @Test
    void getValidCurrency_empty_returnsNull() {
        assertNull(CurrencyUtil.getValidCurrency(""));
    }

    @Test
    void getValidCurrency_null_returnsNull() {
        assertNull(CurrencyUtil.getValidCurrency(null));
    }

    @Test
    void getCurrency_knownNumericCode_returnsAlpha3() {
        assertEquals("USD", CurrencyUtil.getCurrency(840));
        assertEquals("EUR", CurrencyUtil.getCurrency(978));
        assertEquals("RUB", CurrencyUtil.getCurrency(643));
    }

    @Test
    void getCurrency_unknownNumericCode_returnsNull() {
        assertNull(CurrencyUtil.getCurrency(999999));
    }

    @Test
    void getCurrency_null_returnsNull() {
        assertNull(CurrencyUtil.getCurrency(null));
    }
}
