package com.grunvald.sorm.service.whmcsdata.dataservice.user.model;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private User user(String fullName, String firstName, String lastName, String email) {
        var u = new User();
        u.setId(1L);
        u.setFullName(fullName);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setEmail(email);
        u.setDateCreated(OffsetDateTime.parse("2026-01-01T00:00:00Z"));
        return u;
    }

    @Test
    void update_sameValues_returnsFalse() {
        var local = user("Ivan Petrov", "Ivan", "Petrov", "i@m.com");
        var incoming = user("Ivan Petrov", "Ivan", "Petrov", "i@m.com");

        assertFalse(local.update(incoming));
    }

    @Test
    void update_changedFields_appliedAndReturnsTrue() {
        var local = user("Ivan Petrov", "Ivan", "Petrov", "i@m.com");
        var incoming = user("Ivan Sidorov", "Ivan", "Sidorov", "new@m.com");

        assertTrue(local.update(incoming));

        assertEquals("Ivan Sidorov", local.getFullName());
        assertEquals("Ivan", local.getFirstName());
        assertEquals("Sidorov", local.getLastName());
        assertEquals("new@m.com", local.getEmail());
    }

    @Test
    void update_idDateCreated_neverCopied() {
        var local = user("Ivan Petrov", "Ivan", "Petrov", "i@m.com");
        var incoming = user("Ivan Petrov", "Ivan", "Petrov", "i@m.com");
        incoming.setId(99L);
        incoming.setDateCreated(OffsetDateTime.parse("1999-01-01T00:00:00Z"));

        assertFalse(local.update(incoming));

        assertEquals(1L, local.getId());
        assertEquals(OffsetDateTime.parse("2026-01-01T00:00:00Z"), local.getDateCreated());
    }
}
