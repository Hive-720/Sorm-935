package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ClientLogin;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpAddressBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.clientLoginPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientLoginPresentationMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ClientLoginPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = clientLoginPresentationMapper();
    }

    @Test
    void shouldMapAllFields() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin(1L, "10.0.0.1", DATE);
        source.setId(9L);

        ClientLogin result = mapper.map(source);

        assertEquals(9L, result.id());
        assertEquals(1L, result.clientId());
        assertEquals(new IpAddressBase("10.0.0.1", IpType.IPV4), result.ipAddress());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.date());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
