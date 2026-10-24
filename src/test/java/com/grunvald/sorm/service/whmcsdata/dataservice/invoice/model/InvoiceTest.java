package com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvoiceTest {

    private Invoice invoice(String status, List<Invoice.InvoiceItem> items) {
        var i = new Invoice();
        i.setId(1L);
        i.setClientId(5L);
        i.setStatus(status);
        i.setItems(items);
        i.setDate(OffsetDateTime.parse("2026-01-01T00:00:00Z"));
        return i;
    }

    private Invoice.InvoiceItem item(long id, String type, String amount) {
        var it = new Invoice.InvoiceItem();
        it.setId(id);
        it.setType(type);
        it.setAmount(new BigDecimal(amount));
        return it;
    }

    @Test
    void update_sameValues_returnsFalse() {
        var local = invoice("Unpaid", List.of(item(1, "t", "10")));
        var incoming = invoice("Unpaid", List.of(item(1, "t", "10.0")));

        assertFalse(local.update(incoming));
    }

    @Test
    void update_changedScalars_appliedAndReturnsTrue() {
        var local = invoice("Unpaid", null);
        local.setTotal(new BigDecimal("100"));
        var incoming = invoice("Paid", null);
        incoming.setTotal(new BigDecimal("200"));
        incoming.setCurrency("USD");
        incoming.setPaymentMethod("stripe");
        incoming.setDueDate(OffsetDateTime.parse("2026-02-01T00:00:00Z"));
        incoming.setDatePaid(OffsetDateTime.parse("2026-02-02T00:00:00Z"));
        incoming.setDateRefunded(OffsetDateTime.parse("2026-02-03T00:00:00Z"));
        incoming.setDateCancelled(OffsetDateTime.parse("2026-02-04T00:00:00Z"));
        incoming.setUpdatedAt(OffsetDateTime.parse("2026-02-05T00:00:00Z"));
        incoming.setSubTotal(new BigDecimal("150"));
        incoming.setCredit(new BigDecimal("5"));
        incoming.setBalance(new BigDecimal("195"));
        incoming.setTax(new BigDecimal("20"));
        incoming.setTax2(new BigDecimal("1"));
        incoming.setTaxRate(new BigDecimal("18"));
        incoming.setTaxRate2(new BigDecimal("2"));

        assertTrue(local.update(incoming));

        assertEquals("Paid", local.getStatus());
        assertEquals(0, local.getTotal().compareTo(new BigDecimal("200")));
        assertEquals("USD", local.getCurrency());
        assertEquals("stripe", local.getPaymentMethod());
        assertEquals(OffsetDateTime.parse("2026-02-01T00:00:00Z"), local.getDueDate());
        assertEquals(0, local.getSubTotal().compareTo(new BigDecimal("150")));
        assertEquals(0, local.getTaxRate().compareTo(new BigDecimal("18")));
    }

    @Test
    void update_itemsChanged_appliesAndReturnsTrue() {
        var local = invoice("Unpaid", List.of(item(1, "t", "10")));
        var incoming = invoice("Unpaid", List.of(item(1, "t", "99")));

        assertTrue(local.update(incoming));
        assertEquals(0, local.getItems().get(0).getAmount().compareTo(new BigDecimal("99")));
    }

    @Test
    void update_itemsClearedToNull_applies() {
        var local = invoice("Unpaid", List.of(item(1, "t", "10")));
        var incoming = invoice("Unpaid", null);

        assertTrue(local.update(incoming));
        assertEquals(null, local.getItems());
    }

    @Test
    void update_idClientIdDateCreatedAt_neverCopied() {
        var local = invoice("Unpaid", null);
        var incoming = invoice("Unpaid", null);
        incoming.setId(99L);
        incoming.setClientId(55L);
        incoming.setDate(OffsetDateTime.parse("1999-01-01T00:00:00Z"));

        assertFalse(local.update(incoming));

        assertEquals(1L, local.getId());
        assertEquals(5L, local.getClientId());
        assertEquals(OffsetDateTime.parse("2026-01-01T00:00:00Z"), local.getDate());
    }
}
