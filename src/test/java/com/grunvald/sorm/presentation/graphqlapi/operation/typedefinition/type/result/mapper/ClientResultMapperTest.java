package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.PersonBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ClientResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.clientResultMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientResultMapperTest {

    private ClientResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = clientResultMapper();
    }

    @Test
    void shouldMapSearchResultToResult() {
        var first = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client();
        first.setId(1L);
        first.setFullName("Ivanov Ivan");
        first.setFirstName("Ivan");
        var second = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client();
        second.setId(2L);
        second.setFullName("Petrov Petr");
        second.setFirstName("Petr");

        ClientResult result = mapper.map(new ClientSearchResult("cursor-1", true, List.of(first, second)));

        assertEquals("cursor-1", result.cursor());
        assertEquals(true, result.hasNextPage());
        assertEquals(2, result.result().size());
        assertEquals(new PersonBase("Ivanov Ivan", "Ivan", null), result.result().get(0).name());
        assertEquals(new PersonBase("Petrov Petr", "Petr", null), result.result().get(1).name());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
