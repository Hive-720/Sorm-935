package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.OrderResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.dto.OrderSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.orderResultMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderResultMapperTest {

    private OrderResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = orderResultMapper();
    }

    @Test
    void shouldMapSearchResultToResult() {
        var first = new com.grunvald.sorm.service.whmcsdata.dataservice.order.model.Order();
        first.setId(1L);
        first.setStatus("Pending");
        var second = new com.grunvald.sorm.service.whmcsdata.dataservice.order.model.Order();
        second.setId(2L);
        second.setStatus("Active");

        OrderResult result = mapper.map(new OrderSearchResult("cursor-1", true, List.of(first, second)));

        assertEquals("cursor-1", result.cursor());
        assertEquals(true, result.hasNextPage());
        assertEquals(2, result.result().size());
        assertEquals(1L, result.result().get(0).id());
        assertEquals("Pending", result.result().get(0).status());
        assertEquals(2L, result.result().get(1).id());
        assertEquals("Active", result.result().get(1).status());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
