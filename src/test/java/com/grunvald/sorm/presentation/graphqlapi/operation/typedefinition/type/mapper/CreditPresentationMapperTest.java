package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Credit;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.creditPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreditPresentationMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private CreditPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = creditPresentationMapper();
    }

    @Test
    void shouldMapAllFields() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit();
        source.setId(42L);
        source.setClientId(7L);
        source.setRelId(99L);
        source.setDescription("bonus");
        source.setAmount(new BigDecimal("10.50"));
        source.setDate(DATE);

        Credit result = mapper.map(source);

        assertEquals("42", result.id());
        assertEquals(7L, result.clientId());
        assertEquals("99", result.relId());
        assertEquals("bonus", result.description());
        assertEquals("10.50", result.amount());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.date());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
