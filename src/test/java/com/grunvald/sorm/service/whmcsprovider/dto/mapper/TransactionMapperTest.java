package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction;
import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceAdditionalDataProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransactionMapperTest {

    private final TransactionMapper mapper = new TransactionMapperImpl();

    @Test
    void map_fullDto_mapsAllFieldsAndCurrencyParam() {
        var dto = new InvoiceAdditionalDataProviderDto.TransactionDto();
        dto.setId(1L);
        dto.setClientId(2L);
        dto.setInvoiceId(9L);
        dto.setRefundId(5L);
        dto.setTransId("T1");
        dto.setGateway("paypal");
        dto.setDescription("description");
        dto.setAmountIn(new BigDecimal("10.0"));
        dto.setFees(new BigDecimal("0.3"));
        dto.setAmountOut(new BigDecimal("1.0"));
        dto.setRate(new BigDecimal("2.5"));
        dto.setDate(OffsetDateTime.parse("2026-01-02T03:04:05Z"));

        Transaction result = mapper.map(dto, "USD");

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
        assertEquals("USD", result.getCurrency());
    }

    @Test
    void map_bothNull_returnsNull() {
        assertNull(mapper.map(null, null));
    }

    @Test
    void map_nullDto_passesCurrencyParam() {
        Transaction result = mapper.map(null, "USD");

        assertNotNull(result);
        assertEquals("USD", result.getCurrency());
        assertNull(result.getId());
        assertNull(result.getDescription());
    }

    @Test
    void map_nullCurrency_mapsNullCurrency() {
        var dto = new InvoiceAdditionalDataProviderDto.TransactionDto();
        dto.setId(1L);

        Transaction result = mapper.map(dto, null);

        assertNotNull(result);
        assertNull(result.getCurrency());
        assertEquals(1L, result.getId());
    }
}
