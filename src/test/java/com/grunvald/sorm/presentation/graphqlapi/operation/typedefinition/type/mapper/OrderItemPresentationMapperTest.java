package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.OrderItem;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DomainNameBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.grunvald.sorm.testutil.MapStructTestFactory.orderItemPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderItemPresentationMapperTest {

    private OrderItemPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = orderItemPresentationMapper();
    }

    @Test
    void shouldMapAllFields() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem();
        source.setId(99L);
        source.setOrderId(1L);
        source.setRelId(2L);
        source.setType("Hosting");
        source.setProductType("vps");
        source.setProductName("VPS-1");
        source.setDomain("example.com");
        source.setBillingCycle(new BigDecimal("1"));
        source.setAmount(new BigDecimal("10.50"));
        source.setStatus("Active");

        OrderItem result = mapper.map(source);

        assertEquals(1L, result.orderId());
        assertEquals("Hosting", result.type());
        assertEquals(2L, result.relId());
        assertEquals("vps", result.productType());
        assertEquals("VPS-1", result.productName());
        assertEquals(new DomainNameBase("example.com"), result.domain());
        assertEquals("1", result.billingCycle());
        assertEquals("10.50", result.amount());
        assertEquals("Active", result.status());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
