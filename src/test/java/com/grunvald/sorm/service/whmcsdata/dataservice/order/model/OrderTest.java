package com.grunvald.sorm.service.whmcsdata.dataservice.order.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderTest {

    private Order order(BigDecimal amount, String currency, String status, String paymentMethod,
                        String paymentMethodName, String paymentStatus, OffsetDateTime date) {
        var o = new Order();
        o.setId(1L);
        o.setClientId(5L);
        o.setContactId(6L);
        o.setInvoiceId(7L);
        o.setAmount(amount);
        o.setCurrency(currency);
        o.setStatus(status);
        o.setPaymentMethod(paymentMethod);
        o.setPaymentMethodName(paymentMethodName);
        o.setPaymentStatus(paymentStatus);
        o.setDate(date);
        return o;
    }

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-01T00:00:00Z");

    @Test
    void update_sameValues_returnsFalse() {
        var local = order(new BigDecimal("10.00"), "RUB", "Active", "paypal", "PayPal", "Paid", DATE);
        var incoming = order(new BigDecimal("10.00"), "RUB", "Active", "paypal", "PayPal", "Paid", DATE);

        assertFalse(local.update(incoming));
    }

    @Test
    void update_changedFields_appliedAndReturnsTrue() {
        var local = order(new BigDecimal("10.00"), "RUB", "Active", "paypal", "PayPal", "Paid", DATE);
        var incoming = order(new BigDecimal("11.00"), "USD", "Cancelled", "card", "Card", "Refunded",
                OffsetDateTime.parse("2026-02-01T00:00:00Z"));

        assertTrue(local.update(incoming));

        assertEquals(new BigDecimal("11.00"), local.getAmount());
        assertEquals("USD", local.getCurrency());
        assertEquals("Cancelled", local.getStatus());
        assertEquals("card", local.getPaymentMethod());
        assertEquals("Card", local.getPaymentMethodName());
        assertEquals("Refunded", local.getPaymentStatus());
        assertEquals(OffsetDateTime.parse("2026-02-01T00:00:00Z"), local.getDate());
    }

    @Test
    void update_idClientIdContactIdInvoiceId_neverCopied() {
        var local = order(new BigDecimal("10.00"), "RUB", "Active", "paypal", "PayPal", "Paid", DATE);
        var incoming = order(new BigDecimal("10.00"), "RUB", "Active", "paypal", "PayPal", "Paid", DATE);
        incoming.setId(99L);
        incoming.setClientId(55L);
        incoming.setContactId(66L);
        incoming.setInvoiceId(77L);

        assertFalse(local.update(incoming));

        assertEquals(1L, local.getId());
        assertEquals(5L, local.getClientId());
        assertEquals(6L, local.getContactId());
        assertEquals(7L, local.getInvoiceId());
    }

    @Test
    void update_nullAmounts_appliedWhenDiffer() {
        var local = order(null, "RUB", "Active", "paypal", "PayPal", "Paid", DATE);
        var incoming = order(new BigDecimal("1.00"), "RUB", "Active", "paypal", "PayPal", "Paid", DATE);

        assertTrue(local.update(incoming));
        assertEquals(new BigDecimal("1.00"), local.getAmount());

        assertFalse(local.update(incoming));
    }
}
