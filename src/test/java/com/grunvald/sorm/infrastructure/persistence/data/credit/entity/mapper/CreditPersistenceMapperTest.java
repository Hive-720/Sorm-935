package com.grunvald.sorm.infrastructure.persistence.data.credit.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.credit.entity.CreditDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.creditPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreditPersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private CreditPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = creditPersistenceMapper();
    }

    @Test
    void shouldMapDbToModel() {
        CreditDb db = new CreditDb();
        db.setId(1L);
        db.setClientId(2L);
        db.setRelId(3L);
        db.setDescription("bonus");
        db.setAmount(new BigDecimal("10.50"));
        db.setDate(DATE);

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(3L, result.getRelId());
        assertEquals("bonus", result.getDescription());
        assertEquals(new BigDecimal("10.50"), result.getAmount());
        assertEquals(DATE, result.getDate());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit();
        model.setId(1L);
        model.setClientId(2L);
        model.setRelId(3L);
        model.setDescription("bonus");
        model.setAmount(new BigDecimal("10.50"));
        model.setDate(DATE);

        CreditDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(3L, result.getRelId());
        assertEquals("bonus", result.getDescription());
        assertEquals(new BigDecimal("10.50"), result.getAmount());
        assertEquals(DATE, result.getDate());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((CreditDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit) null));
    }
}
