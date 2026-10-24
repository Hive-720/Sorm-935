package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.config.Config;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetInvoiceResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceAdditionalDataProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransactionProviderMapperTest {

    private static final ObjectMapper OBJECT_MAPPER = new Config().objectMapper();

    private final TransactionProviderMapper mapper = new TransactionProviderMapperImpl();

    private static <T> T fromJson(String json, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Failed to parse fixture json", e);
        }
    }

    @Test
    void map_fullTransaction_mapsAllFields() {
        var source = fromJson("""
                {
                  "id": 1,
                  "userid": 2,
                  "currency": "USD",
                  "gateway": "paypal",
                  "date": "2026-01-02 03:04:05",
                  "description": "description",
                  "amountin": 10.0,
                  "fees": 0.3,
                  "amountout": 1.0,
                  "rate": 2.5,
                  "transid": "T1",
                  "invoiceid": 9,
                  "refundid": 5
                }
                """, GetInvoiceResponse.Transactions.Transaction.class);

        InvoiceAdditionalDataProviderDto.TransactionDto result = mapper.map(source);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(9L, result.getInvoiceId());
        assertEquals(5L, result.getRefundId());
        assertEquals("T1", result.getTransId());
        assertEquals("paypal", result.getGateway());
        assertEquals("description", result.getDescription());
        assertEquals(new BigDecimal("10.0"), result.getAmountIn());
        assertEquals(new BigDecimal("0.3"), result.getFees());
        assertEquals(new BigDecimal("1.0"), result.getAmountOut());
        assertEquals(new BigDecimal("2.5"), result.getRate());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDate());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
