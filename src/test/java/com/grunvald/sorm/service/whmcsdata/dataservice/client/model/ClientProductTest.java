package com.grunvald.sorm.service.whmcsdata.dataservice.client.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientProductTest {

    private ClientProduct product(String name, String domain) {
        var p = new ClientProduct();
        p.setId(1L);
        p.setName(name);
        p.setDomain(domain);
        return p;
    }

    @Test
    void update_nullIncoming_returnsFalse() {
        var local = product("p", "d");

        assertFalse(local.update(null));
    }

    @Test
    void update_sameValues_returnsFalse() {
        var local = product("p", "d");
        local.setAssignedIpAddresses(List.of("1.1.1.1"));
        var incoming = product("p", "d");
        incoming.setAssignedIpAddresses(List.of("1.1.1.1"));

        assertFalse(local.update(incoming));
    }

    @Test
    void update_changedScalarFields_appliedAndReturnsTrue() {
        var local = product("old", "old.d");
        var incoming = product("new", "new.d");
        incoming.setDedicatedIpAddress("2.2.2.2");
        incoming.setFirstPaymentAmount(new BigDecimal("10.0"));
        incoming.setRecurringAmount(new BigDecimal("20.0"));
        incoming.setBillingCycle("Monthly");
        incoming.setStatus("Active");
        incoming.setNextDueDate(OffsetDateTime.parse("2026-02-01T00:00:00Z"));
        incoming.setLastUpdate(OffsetDateTime.parse("2026-02-02T00:00:00Z"));

        assertTrue(local.update(incoming));

        assertEquals("new", local.getName());
        assertEquals("new.d", local.getDomain());
        assertEquals("2.2.2.2", local.getDedicatedIpAddress());
        assertEquals(0, local.getFirstPaymentAmount().compareTo(new BigDecimal("10.0")));
        assertEquals(0, local.getRecurringAmount().compareTo(new BigDecimal("20.0")));
        assertEquals("Monthly", local.getBillingCycle());
        assertEquals("Active", local.getStatus());
        assertEquals(OffsetDateTime.parse("2026-02-01T00:00:00Z"), local.getNextDueDate());
        assertEquals(OffsetDateTime.parse("2026-02-02T00:00:00Z"), local.getLastUpdate());
    }

    @Test
    void update_assignedIpAddressesChanged_applied() {
        var local = product("p", "d");
        local.setAssignedIpAddresses(List.of("1.1.1.1"));
        var incoming = product("p", "d");
        incoming.setAssignedIpAddresses(List.of("2.2.2.2", "3.3.3.3"));

        assertTrue(local.update(incoming));
        assertEquals(List.of("2.2.2.2", "3.3.3.3"), local.getAssignedIpAddresses());
    }

    @Test
    void update_configsChanged_applied() {
        var local = product("p", "d");
        local.setConfigs(List.of(configValue(1L, "option", "type", "old")));
        var incoming = product("p", "d");
        incoming.setConfigs(List.of(configValue(1L, "option", "type", "new")));

        assertTrue(local.update(incoming));
        assertEquals("new", local.getConfigs().get(0).getValue());
    }

    @Test
    void update_idAndClientId_neverCopied() {
        var local = product("p", "d");
        var incoming = product("p", "d");
        incoming.setId(99L);

        assertFalse(local.update(incoming));
        assertEquals(1L, local.getId());
    }

    private ClientProduct.ConfigValue configValue(Long id, String option, String type, String value) {
        var cv = new ClientProduct.ConfigValue();
        cv.setId(id);
        cv.setOption(option);
        cv.setType(type);
        cv.setValue(value);
        return cv;
    }
}
