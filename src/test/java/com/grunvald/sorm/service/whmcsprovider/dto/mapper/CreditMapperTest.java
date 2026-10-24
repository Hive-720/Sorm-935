package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit;
import com.grunvald.sorm.service.whmcsprovider.dto.CreditProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreditMapperTest {

    private final CreditMapper mapper = new CreditMapperImpl();

    @Test
    void map_fullDto_mapsAllFieldsAndClientIdParam() {
        var dto = new CreditProviderDto();
        dto.setId(1L);
        dto.setDate(OffsetDateTime.parse("2026-01-02T03:04:05Z"));
        dto.setDescription("description");
        dto.setAmount(new BigDecimal("10.5"));
        dto.setRelId(9L);

        Credit result = mapper.map(dto, 5L);

        assertEquals(1L, result.getId());
        assertEquals(5L, result.getClientId());
        assertEquals(9L, result.getRelId());
        assertEquals("description", result.getDescription());
        assertEquals(new BigDecimal("10.5"), result.getAmount());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDate());
    }

    @Test
    void map_bothNull_returnsNull() {
        assertNull(mapper.map(null, null));
    }

    @Test
    void map_nullDto_passesClientIdParam() {
        Credit result = mapper.map(null, 5L);

        assertNotNull(result);
        assertEquals(5L, result.getClientId());
        assertNull(result.getId());
        assertNull(result.getDescription());
    }

    @Test
    void map_nullClientId_mapsNullClientId() {
        var dto = new CreditProviderDto();
        dto.setId(1L);

        Credit result = mapper.map(dto, null);

        assertNotNull(result);
        assertNull(result.getClientId());
        assertEquals(1L, result.getId());
    }
}
