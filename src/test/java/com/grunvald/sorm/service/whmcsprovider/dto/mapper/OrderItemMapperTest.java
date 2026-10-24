package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem;
import com.grunvald.sorm.service.whmcsprovider.dto.OrderProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderItemMapperTest {

    private final OrderItemMapper mapper = new OrderItemMapperImpl();

    @Test
    void map_fullLineItem_mapsAllFieldsAndOrderIdParam() {
        var lineItem = new OrderProviderDto.LineItem();
        lineItem.setType("product");
        lineItem.setRelId(1L);
        lineItem.setProductType("hosting");
        lineItem.setProduct("Plan X");
        lineItem.setDomain("example.com");
        lineItem.setBillingCycle(new BigDecimal("12"));
        lineItem.setAmount(new BigDecimal("10.5"));
        lineItem.setStatus("s");

        OrderItem result = mapper.map(lineItem, 5L);

        assertNull(result.getId());
        assertEquals(5L, result.getOrderId());
        assertEquals(1L, result.getRelId());
        assertEquals("product", result.getType());
        assertEquals("hosting", result.getProductType());
        assertEquals("Plan X", result.getProductName());
        assertEquals("example.com", result.getDomain());
        assertEquals(new BigDecimal("12"), result.getBillingCycle());
        assertEquals(new BigDecimal("10.5"), result.getAmount());
        assertEquals("s", result.getStatus());
    }

    @Test
    void map_bothNull_returnsNull() {
        assertNull(mapper.map(null, null));
    }

    @Test
    void map_nullLineItem_passesOrderIdParam() {
        OrderItem result = mapper.map(null, 5L);

        assertNotNull(result);
        assertEquals(5L, result.getOrderId());
        assertNull(result.getProductName());
        assertNull(result.getType());
    }
}
