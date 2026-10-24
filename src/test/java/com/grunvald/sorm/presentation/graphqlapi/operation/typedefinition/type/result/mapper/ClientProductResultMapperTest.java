package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ClientProductResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientProductSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.clientProductResultMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientProductResultMapperTest {

    private ClientProductResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = clientProductResultMapper();
    }

    @Test
    void shouldMapSearchResultToResult() {
        var first = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct();
        first.setId(1L);
        first.setName("VPS-1");
        var second = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct();
        second.setId(2L);
        second.setName("VPS-2");

        ClientProductResult result = mapper.map(new ClientProductSearchResult("cursor-1", false, List.of(first, second)));

        assertEquals("cursor-1", result.cursor());
        assertEquals(false, result.hasNextPage());
        assertEquals(2, result.result().size());
        assertEquals("1", result.result().get(0).id());
        assertEquals("VPS-1", result.result().get(0).name());
        assertEquals("2", result.result().get(1).id());
        assertEquals("VPS-2", result.result().get(1).name());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
