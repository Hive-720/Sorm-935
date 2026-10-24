package com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AffiliateTest {

    private static final OffsetDateTime UPDATED_AT = OffsetDateTime.parse("2026-01-01T00:00:00Z");

    private Affiliate affiliate(Long visitors, String payType, BigDecimal payAmount, Boolean oneTime,
                                BigDecimal balance, BigDecimal withdrawn, OffsetDateTime updatedAt) {
        var a = new Affiliate();
        a.setId(1L);
        a.setClientId(5L);
        a.setVisitors(visitors);
        a.setPayType(payType);
        a.setPayAmount(payAmount);
        a.setOneTime(oneTime);
        a.setBalance(balance);
        a.setWithdrawn(withdrawn);
        a.setDate(UPDATED_AT);
        a.setCreatedAt(UPDATED_AT);
        a.setUpdatedAt(updatedAt);
        return a;
    }

    @Test
    void update_sameValues_returnsFalse() {
        var local = affiliate(10L, "onetime", new BigDecimal("5.00"), true,
                new BigDecimal("1.00"), new BigDecimal("2.00"), UPDATED_AT);
        var incoming = affiliate(10L, "onetime", new BigDecimal("5.00"), true,
                new BigDecimal("1.00"), new BigDecimal("2.00"), UPDATED_AT);

        assertFalse(local.update(incoming));
    }

    @Test
    void update_changedFields_appliedAndReturnsTrue() {
        var local = affiliate(10L, "onetime", new BigDecimal("5.00"), true,
                new BigDecimal("1.00"), new BigDecimal("2.00"), UPDATED_AT);
        var incoming = affiliate(11L, "percentage", new BigDecimal("6.00"), false,
                new BigDecimal("3.00"), new BigDecimal("4.00"),
                OffsetDateTime.parse("2026-02-01T00:00:00Z"));

        assertTrue(local.update(incoming));

        assertEquals(11L, local.getVisitors());
        assertEquals("percentage", local.getPayType());
        assertEquals(new BigDecimal("6.00"), local.getPayAmount());
        assertEquals(false, local.getOneTime());
        assertEquals(new BigDecimal("3.00"), local.getBalance());
        assertEquals(new BigDecimal("4.00"), local.getWithdrawn());
        assertEquals(OffsetDateTime.parse("2026-02-01T00:00:00Z"), local.getUpdatedAt());
    }

    @Test
    void update_idClientIdDateCreatedAt_neverCopied() {
        var local = affiliate(10L, "onetime", new BigDecimal("5.00"), true,
                new BigDecimal("1.00"), new BigDecimal("2.00"), UPDATED_AT);
        var incoming = affiliate(10L, "onetime", new BigDecimal("5.00"), true,
                new BigDecimal("1.00"), new BigDecimal("2.00"), UPDATED_AT);
        incoming.setId(99L);
        incoming.setClientId(55L);
        incoming.setDate(OffsetDateTime.parse("1999-01-01T00:00:00Z"));
        incoming.setCreatedAt(OffsetDateTime.parse("1999-01-01T00:00:00Z"));

        assertFalse(local.update(incoming));

        assertEquals(1L, local.getId());
        assertEquals(5L, local.getClientId());
        assertEquals(UPDATED_AT, local.getDate());
        assertEquals(UPDATED_AT, local.getCreatedAt());
    }
}
