package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Order;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpAddressBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.orderPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderPresentationMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private OrderPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = orderPresentationMapper();
    }

    @Test
    void shouldMapAllFields() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.order.model.Order();
        source.setId(1L);
        source.setClientId(2L);
        source.setContactId(3L);
        source.setInvoiceId(4L);
        source.setAmount(new BigDecimal("100.00"));
        source.setCurrency("RUB");
        source.setStatus("Pending");
        source.setIpAddress("10.0.0.1");
        source.setPaymentMethod("paypal");
        source.setPaymentMethodName("PayPal");
        source.setPaymentStatus("Paid");
        source.setDate(DATE);

        Order result = mapper.map(source);

        assertEquals(1L, result.id());
        assertEquals(2L, result.clientId());
        assertEquals(3L, result.contactId());
        assertEquals(4L, result.invoiceId());
        assertEquals(new BigDecimal("100.00"), result.amount());
        assertEquals("RUB", result.currency());
        assertEquals("Pending", result.status());
        assertEquals(new IpAddressBase("10.0.0.1", IpType.IPV4), result.ipAddress());
        assertEquals("paypal", result.paymentMethod());
        assertEquals("PayPal", result.paymentMethodName());
        assertEquals("Paid", result.paymentStatus());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.date());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
