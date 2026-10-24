package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;
import com.grunvald.sorm.service.whmcsprovider.dto.AffiliateProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AffiliateMapperTest {

    private final AffiliateMapper mapper = new AffiliateMapperImpl();

    @Test
    void map_fullDto_mapsAllFields() {
        var dto = new AffiliateProviderDto();
        dto.setId(1L);
        dto.setClientId(2L);
        dto.setVisitors(100L);
        dto.setPayType("percentage");
        dto.setPayAmount(new BigDecimal("10.5"));
        dto.setOneTime(true);
        dto.setBalance(new BigDecimal("20.5"));
        dto.setWithdrawn(new BigDecimal("30.5"));
        dto.setDate(OffsetDateTime.parse("2026-01-02T03:04:05Z"));
        dto.setCreatedAt(OffsetDateTime.parse("2026-02-02T03:04:05Z"));
        dto.setUpdatedAt(OffsetDateTime.parse("2026-03-02T03:04:05Z"));

        Affiliate result = mapper.map(dto);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(100L, result.getVisitors());
        assertEquals("percentage", result.getPayType());
        assertEquals(new BigDecimal("10.5"), result.getPayAmount());
        assertEquals(Boolean.TRUE, result.getOneTime());
        assertEquals(new BigDecimal("20.5"), result.getBalance());
        assertEquals(new BigDecimal("30.5"), result.getWithdrawn());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDate());
        assertEquals(OffsetDateTime.parse("2026-02-02T03:04:05Z"), result.getCreatedAt());
        assertEquals(OffsetDateTime.parse("2026-03-02T03:04:05Z"), result.getUpdatedAt());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
