package com.grunvald.sorm.service.whmcsdata.dataservice.order.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderItemTest {

    private OrderItem item(String type, String productType, String productName, String domain,
                           BigDecimal billingCycle, BigDecimal amount, String status) {
        var i = new OrderItem();
        i.setId(1L);
        i.setOrderId(5L);
        i.setRelId(6L);
        i.setType(type);
        i.setProductType(productType);
        i.setProductName(productName);
        i.setDomain(domain);
        i.setBillingCycle(billingCycle);
        i.setAmount(amount);
        i.setStatus(status);
        return i;
    }

    @Test
    void update_sameValues_returnsFalse() {
        var local = item("Hosting", "shared", "Plan A", "d.ru", new BigDecimal("1"), new BigDecimal("10.00"), "Active");
        var incoming = item("Hosting", "shared", "Plan A", "d.ru", new BigDecimal("1"), new BigDecimal("10.00"), "Active");

        assertFalse(local.update(incoming));
    }

    @Test
    void update_changedFields_appliedAndReturnsTrue() {
        var local = item("Hosting", "shared", "Plan A", "d.ru", new BigDecimal("1"), new BigDecimal("10.00"), "Active");
        var incoming = item("Domain", "domain", "Plan B", "new.ru", new BigDecimal("2"), new BigDecimal("11.00"), "Cancelled");

        assertTrue(local.update(incoming));

        assertEquals("Domain", local.getType());
        assertEquals("domain", local.getProductType());
        assertEquals("Plan B", local.getProductName());
        assertEquals("new.ru", local.getDomain());
        assertEquals(new BigDecimal("2"), local.getBillingCycle());
        assertEquals(new BigDecimal("11.00"), local.getAmount());
        assertEquals("Cancelled", local.getStatus());
    }

    @Test
    void update_idOrderIdRelId_neverCopied() {
        var local = item("Hosting", "shared", "Plan A", "d.ru", new BigDecimal("1"), new BigDecimal("10.00"), "Active");
        var incoming = item("Hosting", "shared", "Plan A", "d.ru", new BigDecimal("1"), new BigDecimal("10.00"), "Active");
        incoming.setId(99L);
        incoming.setOrderId(55L);
        incoming.setRelId(66L);

        assertFalse(local.update(incoming));

        assertEquals(1L, local.getId());
        assertEquals(5L, local.getOrderId());
        assertEquals(6L, local.getRelId());
    }
}
