package com.grunvald.sorm.infrastructure.persistence.data.order.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.order.entity.OrderItemDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.grunvald.sorm.testutil.MapStructTestFactory.orderItemPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderItemPersistenceMapperTest {

    private OrderItemPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = orderItemPersistenceMapper();
    }

    @Test
    void shouldMapDbToModel() {
        OrderItemDb db = new OrderItemDb();
        db.setId(1L);
        db.setOrderId(2L);
        db.setRelId(3L);
        db.setType("Hosting");
        db.setProductType("vps");
        db.setProductName("VPS-1");
        db.setDomain("example.com");
        db.setBillingCycle(new BigDecimal("1"));
        db.setAmount(new BigDecimal("10.50"));
        db.setStatus("Active");

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getOrderId());
        assertEquals(3L, result.getRelId());
        assertEquals("Hosting", result.getType());
        assertEquals("vps", result.getProductType());
        assertEquals("VPS-1", result.getProductName());
        assertEquals("example.com", result.getDomain());
        assertEquals(new BigDecimal("1"), result.getBillingCycle());
        assertEquals(new BigDecimal("10.50"), result.getAmount());
        assertEquals("Active", result.getStatus());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem();
        model.setId(1L);
        model.setOrderId(2L);
        model.setRelId(3L);
        model.setType("Hosting");
        model.setProductType("vps");
        model.setProductName("VPS-1");
        model.setDomain("example.com");
        model.setBillingCycle(new BigDecimal("1"));
        model.setAmount(new BigDecimal("10.50"));
        model.setStatus("Active");

        OrderItemDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getOrderId());
        assertEquals(3L, result.getRelId());
        assertEquals("Hosting", result.getType());
        assertEquals("vps", result.getProductType());
        assertEquals("VPS-1", result.getProductName());
        assertEquals("example.com", result.getDomain());
        assertEquals(new BigDecimal("1"), result.getBillingCycle());
        assertEquals(new BigDecimal("10.50"), result.getAmount());
        assertEquals("Active", result.getStatus());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((OrderItemDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem) null));
    }
}
