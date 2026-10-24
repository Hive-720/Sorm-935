package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetCreditsResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.CreditProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreditProviderMapperTest {

    private final CreditProviderMapper mapper = new CreditProviderMapperImpl();

    @Test
    void map_fullCredit_mapsAllFields() {
        var source = new GetCreditsResponse.Credits.Credit();
        source.setId(1L);
        source.setDate(OffsetDateTime.parse("2026-01-02T03:04:05Z"));
        source.setDescription("description");
        source.setAmount(new BigDecimal("10.5"));
        source.setRelId(9L);

        CreditProviderDto result = mapper.map(source);

        assertEquals(1L, result.getId());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDate());
        assertEquals("description", result.getDescription());
        assertEquals(new BigDecimal("10.5"), result.getAmount());
        assertEquals(9L, result.getRelId());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
