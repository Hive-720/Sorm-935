package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.PaymentMethod;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.paymentMethodPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentMethodPresentationMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private PaymentMethodPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = paymentMethodPresentationMapper();
    }

    @Test
    void shouldMapAllFields() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod();
        source.setId(1L);
        source.setModule("paypal");
        source.setDisplayName("PayPal");
        source.setUpdatedAt(DATE);
        source.setDeleted(true);

        PaymentMethod result = mapper.map(source);

        assertEquals(1L, result.id());
        assertEquals("paypal", result.moduleName());
        assertEquals("PayPal", result.displayName());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.updatedAt());
        assertEquals(true, result.deleted());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
