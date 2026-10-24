package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.Order;
import com.grunvald.sorm.service.whmcsprovider.dto.OrderProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderMapperTest {

    private final OrderMapper mapper = new OrderMapperImpl();

    @Test
    void map_fullDto_mapsAllFields() {
        var dto = new OrderProviderDto();
        dto.setId(1L);
        dto.setClientId(2L);
        dto.setContactId(3L);
        dto.setInvoiceId(9L);
        dto.setAmount(new BigDecimal("12.5"));
        dto.setCurrency("USD");
        dto.setStatus("Pending");
        dto.setIpAddress("1.2.3.4");
        dto.setPaymentMethod("paypal");
        dto.setPaymentMethodName("PayPal");
        dto.setPaymentStatus("Paid");
        dto.setDate(OffsetDateTime.parse("2026-01-02T03:04:05Z"));

        Order result = mapper.map(dto);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(3L, result.getContactId());
        assertEquals(9L, result.getInvoiceId());
        assertEquals(new BigDecimal("12.5"), result.getAmount());
        assertEquals("USD", result.getCurrency());
        assertEquals("Pending", result.getStatus());
        assertEquals("1.2.3.4", result.getIpAddress());
        assertEquals("paypal", result.getPaymentMethod());
        assertEquals("PayPal", result.getPaymentMethodName());
        assertEquals("Paid", result.getPaymentStatus());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDate());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
