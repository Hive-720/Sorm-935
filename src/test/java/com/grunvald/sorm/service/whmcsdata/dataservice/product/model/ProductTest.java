package com.grunvald.sorm.service.whmcsdata.dataservice.product.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTest {

    private Product product(String name, List<Price> prices) {
        var p = new Product();
        p.setId(1L);
        p.setName(name);
        p.setPrices(prices);
        return p;
    }

    private Price price(String currency, String monthly) {
        var p = new Price();
        p.setCurrency(currency);
        p.setMonthly(new BigDecimal(monthly));
        return p;
    }

    @Test
    void update_sameValues_returnsFalse() {
        var local = product("p", List.of(price("RUB", "10")));
        var incoming = product("p", List.of(price("RUB", "10")));

        assertFalse(local.update(incoming));
    }

    @Test
    void update_nameChanged_appliesAndReturnsTrue() {
        var local = product("old", null);
        var incoming = product("new", null);

        assertTrue(local.update(incoming));
        assertEquals("new", local.getName());
    }

    @Test
    void update_pricesChanged_appliesAndReturnsTrue() {
        var local = product("p", List.of(price("RUB", "10")));
        var incoming = product("p", List.of(price("RUB", "99")));

        assertTrue(local.update(incoming));
        assertEquals(0, local.getPrices().get(0).getMonthly().compareTo(new BigDecimal("99")));
    }

    @Test
    void update_pricesSameButScalarChanged_stillDetectsChange() {
        var local = product("old", List.of(price("RUB", "10")));
        var incoming = product("new", List.of(price("RUB", "10")));

        assertTrue(local.update(incoming));
        assertEquals("new", local.getName());
    }

    @Test
    void update_pricesClearedToNull_appliesAndReturnsTrue() {
        var local = product("p", List.of(price("RUB", "10")));
        var incoming = product("p", null);

        assertTrue(local.update(incoming));
        assertEquals(null, local.getPrices());
    }

    @Test
    void update_updatedAtChanged_applies() {
        var local = product("p", null);
        local.setUpdatedAt(OffsetDateTime.parse("2026-01-01T00:00:00Z"));
        var incoming = product("p", null);
        incoming.setUpdatedAt(OffsetDateTime.parse("2026-02-01T00:00:00Z"));

        assertTrue(local.update(incoming));
        assertEquals(OffsetDateTime.parse("2026-02-01T00:00:00Z"), local.getUpdatedAt());
    }

    @Test
    void update_idAndDeleted_neverCopied() {
        var local = product("p", null);
        var incoming = product("p", null);
        incoming.setId(99L);
        incoming.setDeleted(true);

        assertFalse(local.update(incoming));
        assertEquals(1L, local.getId());
        assertFalse(local.isDeleted());
    }
}
