package com.grunvald.sorm.service.whmcsdata.dataservice.product.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductConfigOptionTest {

    private Price price(String currency, String monthly) {
        var p = new Price();
        p.setCurrency(currency);
        p.setMonthly(new BigDecimal(monthly));
        return p;
    }

    private ProductConfigOption option(String name, List<Price> prices) {
        var o = new ProductConfigOption();
        o.setId(1L);
        o.setName(name);
        o.setPrices(prices);
        return o;
    }

    @Test
    void update_sameValues_returnsFalse() {
        var local = option("o", List.of(price("RUB", "10")));
        var incoming = option("o", List.of(price("RUB", "10")));

        assertFalse(local.update(incoming));
    }

    @Test
    void update_nameChanged_appliesAndReturnsTrue() {
        var local = option("old", null);
        var incoming = option("new", null);

        assertTrue(local.update(incoming));
        assertEquals("new", local.getName());
    }

    @Test
    void update_pricesChanged_appliesAndReturnsTrue() {
        var local = option("o", List.of(price("RUB", "10")));
        var incoming = option("o", List.of(price("RUB", "99")));

        assertTrue(local.update(incoming));
        assertEquals(0, local.getPrices().get(0).getMonthly().compareTo(new BigDecimal("99")));
    }

    @Test
    void update_idAndConfigId_neverCopied() {
        var local = option("o", null);
        local.setProductConfigId(5L);
        var incoming = option("o", null);
        incoming.setId(99L);
        incoming.setProductConfigId(55L);

        assertFalse(local.update(incoming));
        assertEquals(1L, local.getId());
        assertEquals(5L, local.getProductConfigId());
    }
}
