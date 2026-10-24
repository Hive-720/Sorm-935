package com.grunvald.sorm.service.whmcsdata.dataservice.contact.model;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContactTest {

    private Contact contact(String email, boolean deleted) {
        var c = new Contact();
        c.setId(1L);
        c.setClientId(5L);
        c.setEmail(email);
        c.setDeleted(deleted);
        c.setCreatedAt(OffsetDateTime.parse("2026-01-01T00:00:00Z"));
        return c;
    }

    @Test
    void update_sameValues_returnsFalse() {
        var local = contact("a@b.c", false);
        var incoming = contact("a@b.c", false);

        assertFalse(local.update(incoming), "равные поля, включая deleted, не должны считаться изменением");
    }

    @Test
    void update_changedFields_appliedAndReturnsTrue() {
        var local = contact("a@b.c", false);
        var incoming = contact("new@b.c", false);
        incoming.setCity("Tula");
        incoming.setUpdatedAt(OffsetDateTime.parse("2026-02-01T00:00:00Z"));

        assertTrue(local.update(incoming));

        assertEquals("new@b.c", local.getEmail());
        assertEquals("Tula", local.getCity());
        assertEquals(OffsetDateTime.parse("2026-02-01T00:00:00Z"), local.getUpdatedAt());
    }

    @Test
    void update_deletedChanged_appliesAndReturnsTrue() {
        var local = contact("a@b.c", false);
        var incoming = contact("a@b.c", true);

        assertTrue(local.update(incoming));
        assertTrue(local.isDeleted());
    }

    @Test
    void update_idClientIdCreatedAt_neverCopied() {
        var local = contact("a@b.c", false);
        var incoming = contact("a@b.c", false);
        incoming.setId(99L);
        incoming.setClientId(55L);
        incoming.setCreatedAt(OffsetDateTime.parse("1999-01-01T00:00:00Z"));

        assertFalse(local.update(incoming));

        assertEquals(1L, local.getId());
        assertEquals(5L, local.getClientId());
        assertEquals(OffsetDateTime.parse("2026-01-01T00:00:00Z"), local.getCreatedAt());
    }
}
