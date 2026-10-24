package com.grunvald.sorm.service.whmcsdata.dataservice.client.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ClientGroupTest {

    private ClientGroup group(String name, String discountPercent, boolean deleted) {
        var g = new ClientGroup(name, discountPercent == null ? null : new BigDecimal(discountPercent));
        g.setDeleted(deleted);
        return g;
    }

    @Test
    void update_sameValues_returnsFalse() {
        var local = group("g", "10.0", false);
        var incoming = group("g", "10.00", false);

        assertFalse(local.update(incoming));
    }

    @Test
    void update_nameChanged_appliedAndReturnsTrue() {
        var local = group("old", "10", false);
        var incoming = group("new", "10", false);

        assertTrue(local.update(incoming));
        assertEquals("new", local.getName());
    }

    @Test
    void update_discountChanged_appliedAndReturnsTrue() {
        var local = group("g", "10", false);
        var incoming = group("g", "20", false);

        assertTrue(local.update(incoming));
        assertEquals(0, local.getDiscountPercent().compareTo(new BigDecimal("20")));
    }

    @Test
    void update_discountFromValueToNull_appliedAndReturnsTrue() {
        var local = group("g", "10", false);
        var incoming = group("g", null, false);

        assertTrue(local.update(incoming));
        assertNull(local.getDiscountPercent());
    }

    @Test
    void update_deletedChanged_appliedAndReturnsTrue() {
        var local = group("g", "10", false);
        var incoming = group("g", "10", true);

        assertTrue(local.update(incoming));
        assertTrue(local.isDeleted());
    }
}
