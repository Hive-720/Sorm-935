package com.grunvald.sorm.service.whmcsdata.dataservice.client.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientTest {

    private Client client(String email, String city) {
        var c = new Client();
        c.setId(1L);
        c.setClientGroupId(5L);
        c.setLastName("Ivanov");
        c.setEmail(email);
        c.setCity(city);
        c.setDateCreated(OffsetDateTime.parse("2026-01-01T00:00:00Z"));
        return c;
    }

    @Test
    void update_sameValues_returnsFalse() {
        var local = client("a@b.c", "Moscow");
        var incoming = client("a@b.c", "Moscow");

        assertFalse(local.update(incoming));
    }

    @Test
    void update_changedFields_appliedAndReturnsTrue() {
        var local = client("a@b.c", "Moscow");
        local.setCredit(new BigDecimal("10.00"));
        local.setTaxExempt(false);

        var incoming = client("new@b.c", "Tula");
        incoming.setCredit(new BigDecimal("20.0"));
        incoming.setTaxExempt(true);
        incoming.setStatus("Active");
        incoming.setLanguage("ru");
        incoming.setDeleted(true);

        assertTrue(local.update(incoming));

        assertEquals("new@b.c", local.getEmail());
        assertEquals("Tula", local.getCity());
        assertEquals(0, local.getCredit().compareTo(new BigDecimal("20.0")), "credit сравнивается по значению");
        assertTrue(local.getTaxExempt());
        assertEquals("Active", local.getStatus());
        assertEquals("ru", local.getLanguage());
        assertTrue(local.isDeleted());
    }

    @Test
    void update_idClientGroupIdLastNameDateCreated_neverCopied() {
        var local = client("a@b.c", "Moscow");
        var incoming = client("a@b.c", "Moscow");
        incoming.setId(99L);
        incoming.setClientGroupId(55L);
        incoming.setLastName("Petrov");
        incoming.setDateCreated(OffsetDateTime.parse("1999-01-01T00:00:00Z"));

        assertFalse(local.update(incoming));

        assertEquals(1L, local.getId());
        assertEquals(5L, local.getClientGroupId());
        assertEquals("Ivanov", local.getLastName());
        assertEquals(OffsetDateTime.parse("2026-01-01T00:00:00Z"), local.getDateCreated());
    }

    @Test
    void update_nullIncomingFields_appliedAsCleared() {
        var local = client("a@b.c", "Moscow");
        var incoming = client(null, null);

        assertTrue(local.update(incoming));
        assertNull(local.getEmail());
        assertNull(local.getCity());
    }
}
