package com.grunvald.sorm.infrastructure.persistence.data.paymentmethod.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.paymentmethod.entity.PaymentMethodDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.paymentMethodPersistenceMapper;
import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodPersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private PaymentMethodPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = paymentMethodPersistenceMapper();
    }

    @Test
    void shouldMapDbToModel() {
        PaymentMethodDb db = new PaymentMethodDb();
        db.setId(1L);
        db.setModule("paypal");
        db.setDisplayName("PayPal");
        db.setUpdatedAt(DATE);
        db.setDeleted(true);

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals("paypal", result.getModule());
        assertEquals("PayPal", result.getDisplayName());
        assertEquals(DATE, result.getUpdatedAt());
        assertTrue(result.isDeleted());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod();
        model.setId(1L);
        model.setModule("paypal");
        model.setDisplayName("PayPal");
        model.setUpdatedAt(DATE);
        model.setDeleted(true);

        PaymentMethodDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals("paypal", result.getModule());
        assertEquals("PayPal", result.getDisplayName());
        assertEquals(DATE, result.getUpdatedAt());
        assertTrue(result.isDeleted());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((PaymentMethodDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod) null));
    }
}
