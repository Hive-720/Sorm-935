package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.config.Config;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetInvoiceResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceAdditionalDataProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InvoiceItemProviderMapperTest {

    private static final ObjectMapper OBJECT_MAPPER = new Config().objectMapper();

    private final InvoiceItemProviderMapper mapper = new InvoiceItemProviderMapperImpl();

    private static <T> T fromJson(String json, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Failed to parse fixture json", e);
        }
    }

    @Test
    void map_fullItem_mapsAllFields() {
        var source = fromJson("""
                {
                  "id": 1,
                  "type": "Hosting",
                  "relid": 3,
                  "description": "description",
                  "amount": 10.5,
                  "taxed": 1
                }
                """, GetInvoiceResponse.Items.Item.class);

        InvoiceAdditionalDataProviderDto.Item result = mapper.map(source);

        assertEquals(1L, result.getId());
        assertEquals("Hosting", result.getType());
        assertEquals(3L, result.getRelId());
        assertEquals("description", result.getDescription());
        assertEquals(new BigDecimal("10.5"), result.getAmount());
        assertEquals(Boolean.TRUE, result.getTaxed());
    }

    @Test
    void map_itemWithTaxedZero_mapsFalse() {
        var source = fromJson("{\"id\": 1, \"relid\": 3, \"taxed\": 0}", GetInvoiceResponse.Items.Item.class);

        InvoiceAdditionalDataProviderDto.Item result = mapper.map(source);

        assertEquals(Boolean.FALSE, result.getTaxed());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
