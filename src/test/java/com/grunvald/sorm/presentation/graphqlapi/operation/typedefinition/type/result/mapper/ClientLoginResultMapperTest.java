package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpAddressBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ClientLoginResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientLoginSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.clientLoginResultMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientLoginResultMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ClientLoginResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = clientLoginResultMapper();
    }

    @Test
    void shouldMapSearchResultToResult() {
        var first = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin(1L, "10.0.0.1", DATE);
        var second = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin(2L, "10.0.0.2", DATE);

        ClientLoginResult result = mapper.map(new ClientLoginSearchResult("cursor-1", true, List.of(first, second)));

        assertEquals("cursor-1", result.cursor());
        assertEquals(true, result.hasNextPage());
        assertEquals(2, result.result().size());
        assertEquals(new IpAddressBase("10.0.0.1", IpType.IPV4), result.result().get(0).ipAddress());
        assertEquals(new IpAddressBase("10.0.0.2", IpType.IPV4), result.result().get(1).ipAddress());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
