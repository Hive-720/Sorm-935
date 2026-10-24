package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.config.Config;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetInvoicesResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InvoiceProviderMapperTest {

    private static final ObjectMapper OBJECT_MAPPER = new Config().objectMapper();

    private final InvoiceProviderMapper mapper = new InvoiceProviderMapperImpl();

    private static <T> T fromJson(String json, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Failed to parse fixture json", e);
        }
    }

    @Test
    void map_fullInvoice_mapsAllFields() {
        // Поля с WhmcsOffsetDateTimeDeserializer ожидают формат "yyyy-MM-dd HH:mm:ss" (без 'T'/'Z'),
        // поле last_capture_attempt без кастомного десериализатора парсится JavaTimeModule как ISO-8601.
        var source = fromJson("""
                {
                  "id": 1,
                  "userid": 2,
                  "invoicenum": "1001",
                  "date": "2026-01-02 03:04:05",
                  "duedate": "2026-02-02 03:04:05",
                  "datepaid": "2026-03-02 03:04:05",
                  "last_capture_attempt": "2026-04-02T03:04:05Z",
                  "date_refunded": "2026-05-02 03:04:05",
                  "date_cancelled": "2026-06-02 03:04:05",
                  "subtotal": 10.0,
                  "credit": 1.0,
                  "tax": 2.0,
                  "tax2": 0.5,
                  "total": 11.5,
                  "taxrate": 20.0,
                  "taxrate2": 5.0,
                  "status": "Paid",
                  "paymentmethod": "paypal",
                  "created_at": "2026-07-02 03:04:05",
                  "updated_at": "2026-08-02 03:04:05",
                  "currencycode": " USD "
                }
                """, GetInvoicesResponse.Invoice.class);

        InvoiceProviderDto result = mapper.map(source);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDate());
        assertEquals(OffsetDateTime.parse("2026-02-02T03:04:05Z"), result.getDueDate());
        assertEquals(OffsetDateTime.parse("2026-03-02T03:04:05Z"), result.getDatePaid());
        assertEquals(OffsetDateTime.parse("2026-05-02T03:04:05Z"), result.getDateRefunded());
        assertEquals(OffsetDateTime.parse("2026-06-02T03:04:05Z"), result.getDateCancelled());
        assertEquals(new BigDecimal("10.0"), result.getSubTotal());
        assertEquals(new BigDecimal("1.0"), result.getCredit());
        assertEquals(new BigDecimal("2.0"), result.getTax());
        assertEquals(new BigDecimal("0.5"), result.getTax2());
        assertEquals(new BigDecimal("11.5"), result.getTotal());
        assertEquals(new BigDecimal("20.0"), result.getTaxRate());
        assertEquals(new BigDecimal("5.0"), result.getTaxRate2());
        assertEquals("Paid", result.getStatus());
        assertEquals("paypal", result.getPaymentMethod());
        assertEquals(OffsetDateTime.parse("2026-07-02T03:04:05Z"), result.getCreatedAt());
        assertEquals(OffsetDateTime.parse("2026-08-02T03:04:05Z"), result.getUpdatedAt());
        assertEquals("USD", result.getCurrencyCode());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
