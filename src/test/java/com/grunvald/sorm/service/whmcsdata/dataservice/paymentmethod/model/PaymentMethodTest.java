package com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentMethodTest {

    private PaymentMethod paymentMethod(String module, String displayName, boolean deleted) {
        var pm = new PaymentMethod();
        pm.setId(1L);
        pm.setModule(module);
        pm.setDisplayName(displayName);
        pm.setDeleted(deleted);
        return pm;
    }

    @Test
    void update_sameValues_returnsFalseAndKeepsState() {
        var local = paymentMethod("mod", "display", false);
        var incoming = paymentMethod("mod", "display", false);

        assertFalse(local.update(incoming));
        assertFalse(local.isDeleted());
    }

    @Test
    void update_displayNameChanged_updatesAndReturnsTrue() {
        var local = paymentMethod("mod", "old", false);
        var incoming = paymentMethod("mod", "new", false);

        assertTrue(local.update(incoming));
        assertFalse(local.update(incoming), "повторный update не должен считать изменение");
    }

    @Test
    void update_deletedFlagChanged_updatesAndReturnsTrue() {
        var local = paymentMethod("mod", "display", false);
        var incoming = paymentMethod("mod", "display", true);

        assertTrue(local.update(incoming));
        assertTrue(local.isDeleted());
    }

    @Test
    void update_nullDisplayName_treatedAsChange() {
        var local = paymentMethod("mod", "display", false);
        var incoming = paymentMethod("mod", null, false);

        assertTrue(local.update(incoming));
        assertFalse(local.update(incoming));
    }

    @Test
    void update_moduleAndId_neverCopiedFromIncoming() {
        var local = paymentMethod("mod", "display", false);
        var incoming = paymentMethod("other-module", "other", false);
        incoming.setId(99L);

        local.update(incoming);

        assertFalse(local.update(incoming), "повторный update после применения incoming не должен видеть изменений");
        assertEquals("mod", local.getModule(), "module не должен меняться через update");
        assertEquals(1L, local.getId());
    }
}
