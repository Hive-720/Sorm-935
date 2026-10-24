package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Affiliate;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.affiliatePresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AffiliatePresentationMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private AffiliatePresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = affiliatePresentationMapper();
    }

    @Test
    void shouldMapAllFields() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate();
        source.setId(1L);
        source.setClientId(2L);
        source.setVisitors(123L);
        source.setPayType("onetime");
        source.setPayAmount(new BigDecimal("10.50"));
        source.setOneTime(true);
        source.setBalance(new BigDecimal("25.00"));
        source.setWithdrawn(new BigDecimal("5.00"));
        source.setDate(DATE);
        source.setCreatedAt(DATE);
        source.setUpdatedAt(DATE);

        Affiliate result = mapper.map(source);

        assertEquals(1L, result.id());
        assertEquals(2L, result.clientId());
        assertEquals(123, result.visitors());
        assertEquals("onetime", result.payType());
        assertEquals("10.50", result.payAmount());
        assertEquals(true, result.oneTime());
        assertEquals(new BigDecimal("25.00"), result.balance());
        assertEquals(new BigDecimal("5.00"), result.withdrawn());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.date());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.createdAt());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.updatedAt());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
