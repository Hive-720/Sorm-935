package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ClientGroupResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientGroupSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.clientGroupResultMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientGroupResultMapperTest {

    private ClientGroupResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = clientGroupResultMapper();
    }

    @Test
    void shouldMapSearchResultToResult() {
        var first = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup("A", new BigDecimal("1.00"));
        first.setId(1L);
        var second = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup("B", new BigDecimal("2.00"));
        second.setId(2L);

        ClientGroupResult result = mapper.map(new ClientGroupSearchResult("cursor-1", true, List.of(first, second)));

        assertEquals("cursor-1", result.cursor());
        assertEquals(true, result.hasNextPage());
        assertEquals(2, result.result().size());
        assertEquals("A", result.result().get(0).name());
        assertEquals(new BigDecimal("1.00"), result.result().get(0).discountPercent());
        assertEquals("B", result.result().get(1).name());
        assertEquals(new BigDecimal("2.00"), result.result().get(1).discountPercent());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
