package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ClientGroup;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.clientGroupPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientGroupPresentationMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ClientGroupPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = clientGroupPresentationMapper();
    }

    @Test
    void shouldMapAllFields() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup("Premium", new BigDecimal("7.50"));
        source.setId(10L);
        source.setUpdatedAt(DATE);
        source.setDeleted(true);

        ClientGroup result = mapper.map(source);

        assertEquals(10L, result.id());
        assertEquals("Premium", result.name());
        assertEquals(new BigDecimal("7.50"), result.discountPercent());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.updatedAt());
        assertEquals(true, result.deleted());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
