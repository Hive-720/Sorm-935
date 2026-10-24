package com.grunvald.sorm.service.whmcsdata.dataservice.product.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductConfigTest {

    @Test
    void update_sameValues_returnsFalse() {
        var local = new ProductConfig(1L, "name", "type");
        var incoming = new ProductConfig(1L, "name", "type");

        assertFalse(local.update(incoming));
    }

    @Test
    void update_nameChanged_appliesAndReturnsTrue() {
        var local = new ProductConfig(1L, "old", "type");
        var incoming = new ProductConfig(1L, "new", "type");

        assertTrue(local.update(incoming));
        assertEquals("new", local.getName());
    }

    @Test
    void update_typeChanged_appliesAndReturnsTrue() {
        var local = new ProductConfig(1L, "name", "old");
        var incoming = new ProductConfig(1L, "name", "new");

        assertTrue(local.update(incoming));
        assertEquals("new", local.getType());
    }
}
