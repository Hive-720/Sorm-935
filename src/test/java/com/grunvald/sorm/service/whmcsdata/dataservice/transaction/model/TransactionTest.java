package com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionTest {

    private Transaction transaction(String gateway, String amountIn) {
        var t = new Transaction();
        t.setId(1L);
        t.setInvoiceId(5L);
        t.setGateway(gateway);
        t.setAmountIn(new BigDecimal(amountIn));
        return t;
    }

    @Test
    void update_sameValues_returnsFalse() {
        var local = transaction("gw", "10.0");
        var incoming = transaction("gw", "10.00");

        assertFalse(local.update(incoming));
    }

    @Test
    void update_changedFields_appliedAndReturnsTrue() {
        var local = transaction("old", "10");
        local.setCurrency("RUB");
        var incoming = transaction("new", "20");
        incoming.setCurrency("USD");
        incoming.setDescription("desc");
        incoming.setRefundId(7L);
        incoming.setFees(new BigDecimal("1.5"));
        incoming.setRate(new BigDecimal("90.0"));
        incoming.setAmountOut(new BigDecimal("5"));

        assertTrue(local.update(incoming));

        assertEquals("new", local.getGateway());
        assertEquals(0, local.getAmountIn().compareTo(new BigDecimal("20")));
        assertEquals("USD", local.getCurrency());
        assertEquals("desc", local.getDescription());
        assertEquals(7L, local.getRefundId());
        assertEquals(0, local.getFees().compareTo(new BigDecimal("1.5")));
        assertEquals(0, local.getRate().compareTo(new BigDecimal("90.0")));
        assertEquals(0, local.getAmountOut().compareTo(new BigDecimal("5")));
    }

    @Test
    void update_idInvoiceIdClientIdDate_neverCopied() {
        var local = transaction("gw", "10");
        var incoming = transaction("gw", "10");
        incoming.setId(99L);
        incoming.setInvoiceId(55L);
        incoming.setClientId(77L);

        assertFalse(local.update(incoming));

        assertEquals(1L, local.getId());
        assertEquals(5L, local.getInvoiceId());
        assertEquals(null, local.getClientId());
    }
}
