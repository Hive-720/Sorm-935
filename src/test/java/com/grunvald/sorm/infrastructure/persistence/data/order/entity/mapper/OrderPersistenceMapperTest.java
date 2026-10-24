package com.grunvald.sorm.infrastructure.persistence.data.order.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.order.entity.OrderDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.orderPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderPersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private OrderPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = orderPersistenceMapper();
    }

    @Test
    void shouldMapDbToModel() {
        OrderDb db = new OrderDb();
        db.setId(1L);
        db.setClientId(2L);
        db.setContactId(3L);
        db.setInvoiceId(4L);
        db.setAmount(new BigDecimal("100.00"));
        db.setCurrency("RUB");
        db.setPaymentMethod("paypal");
        db.setStatus("Pending");
        db.setIpAddress("10.0.0.1");
        db.setPaymentMethodName("PayPal");
        db.setPaymentStatus("Paid");
        db.setDate(DATE);

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(3L, result.getContactId());
        assertEquals(4L, result.getInvoiceId());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals("RUB", result.getCurrency());
        assertEquals("paypal", result.getPaymentMethod());
        assertEquals("Pending", result.getStatus());
        assertEquals("10.0.0.1", result.getIpAddress());
        assertEquals("PayPal", result.getPaymentMethodName());
        assertEquals("Paid", result.getPaymentStatus());
        assertEquals(DATE, result.getDate());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.order.model.Order();
        model.setId(1L);
        model.setClientId(2L);
        model.setContactId(3L);
        model.setInvoiceId(4L);
        model.setAmount(new BigDecimal("100.00"));
        model.setCurrency("RUB");
        model.setPaymentMethod("paypal");
        model.setStatus("Pending");
        model.setIpAddress("10.0.0.1");
        model.setPaymentMethodName("PayPal");
        model.setPaymentStatus("Paid");
        model.setDate(DATE);

        OrderDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(3L, result.getContactId());
        assertEquals(4L, result.getInvoiceId());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals("RUB", result.getCurrency());
        assertEquals("paypal", result.getPaymentMethod());
        assertEquals("Pending", result.getStatus());
        assertEquals("10.0.0.1", result.getIpAddress());
        assertEquals("PayPal", result.getPaymentMethodName());
        assertEquals("Paid", result.getPaymentStatus());
        assertEquals(DATE, result.getDate());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((OrderDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.order.model.Order) null));
    }
}
