package com.grunvald.sorm.service.whmcsdata.dataservice.product.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriceTest {

    private Price price(String currency, String monthly) {
        var p = new Price();
        p.setCurrency(currency);
        p.setMonthly(new BigDecimal(monthly));
        return p;
    }

    @Test
    void isEqual_bothNull_true() {
        assertTrue(Price.isEqual(null, null));
    }

    @Test
    void isEqual_oneNull_false() {
        assertFalse(Price.isEqual(null, List.of(price("RUB", "10"))));
        assertFalse(Price.isEqual(List.of(price("RUB", "10")), null));
    }

    @Test
    void isEqual_equalPrices_true() {
        assertTrue(Price.isEqual(List.of(price("RUB", "10")), List.of(price("RUB", "10.0"))));
    }

    @Test
    void isEqual_differentCurrencies_false() {
        assertFalse(Price.isEqual(List.of(price("RUB", "10")), List.of(price("USD", "10"))));
    }

    @Test
    void isEqual_differentValues_false() {
        assertFalse(Price.isEqual(List.of(price("RUB", "10")), List.of(price("RUB", "20"))));
    }

    @Test
    void isEqual_differentSize_false() {
        assertFalse(Price.isEqual(
                List.of(price("RUB", "10"), price("USD", "1")),
                List.of(price("RUB", "10"))));
    }

    @Test
    void isEqual_emptyLists_true() {
        assertTrue(Price.isEqual(List.of(), List.of()));
    }
}
