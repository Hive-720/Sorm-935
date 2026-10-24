package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetAffiliatesResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.AffiliateProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AffiliateProviderMapperTest {

    private final AffiliateProviderMapper mapper = new AffiliateProviderMapperImpl();

    @Test
    void map_fullAffiliate_mapsAllFields() {
        var source = new GetAffiliatesResponse.Affiliate();
        source.setId(1L);
        source.setDate(OffsetDateTime.parse("2026-01-02T03:04:05Z"));
        source.setClientId(2L);
        source.setVisitors(100L);
        source.setPayType("percentage");
        source.setPayAmount(new BigDecimal("10.5"));
        source.setOneTime(true);
        source.setBalance(new BigDecimal("20.5"));
        source.setWithdrawn(new BigDecimal("30.5"));
        source.setCreatedAt(OffsetDateTime.parse("2026-02-02T03:04:05Z"));
        source.setUpdatedAt(OffsetDateTime.parse("2026-03-02T03:04:05Z"));

        AffiliateProviderDto result = mapper.map(source);

        assertEquals(1L, result.getId());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDate());
        assertEquals(2L, result.getClientId());
        assertEquals(100L, result.getVisitors());
        assertEquals("percentage", result.getPayType());
        assertEquals(new BigDecimal("10.5"), result.getPayAmount());
        assertEquals(Boolean.TRUE, result.getOneTime());
        assertEquals(new BigDecimal("20.5"), result.getBalance());
        assertEquals(new BigDecimal("30.5"), result.getWithdrawn());
        assertEquals(OffsetDateTime.parse("2026-02-02T03:04:05Z"), result.getCreatedAt());
        assertEquals(OffsetDateTime.parse("2026-03-02T03:04:05Z"), result.getUpdatedAt());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
