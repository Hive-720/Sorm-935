package com.grunvald.sorm.infrastructure.persistence.data.affiliate.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.affiliate.entity.AffiliateDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.affiliatePersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AffiliatePersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private AffiliatePersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = affiliatePersistenceMapper();
    }

    @Test
    void shouldMapDbToModel() {
        AffiliateDb db = new AffiliateDb();
        db.setId(1L);
        db.setClientId(2L);
        db.setVisitors(100L);
        db.setPayType("onetime");
        db.setPayAmount(new BigDecimal("10.50"));
        db.setOneTime(true);
        db.setBalance(new BigDecimal("25.00"));
        db.setWithdrawn(new BigDecimal("5.00"));
        db.setDate(DATE);
        db.setCreatedAt(DATE);
        db.setUpdatedAt(DATE);

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(100L, result.getVisitors());
        assertEquals("onetime", result.getPayType());
        assertEquals(new BigDecimal("10.50"), result.getPayAmount());
        assertEquals(true, result.getOneTime());
        assertEquals(new BigDecimal("25.00"), result.getBalance());
        assertEquals(new BigDecimal("5.00"), result.getWithdrawn());
        assertEquals(DATE, result.getDate());
        assertEquals(DATE, result.getCreatedAt());
        assertEquals(DATE, result.getUpdatedAt());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate();
        model.setId(1L);
        model.setClientId(2L);
        model.setVisitors(100L);
        model.setPayType("onetime");
        model.setPayAmount(new BigDecimal("10.50"));
        model.setOneTime(true);
        model.setBalance(new BigDecimal("25.00"));
        model.setWithdrawn(new BigDecimal("5.00"));
        model.setDate(DATE);
        model.setCreatedAt(DATE);
        model.setUpdatedAt(DATE);

        AffiliateDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(100L, result.getVisitors());
        assertEquals("onetime", result.getPayType());
        assertEquals(new BigDecimal("10.50"), result.getPayAmount());
        assertEquals(true, result.getOneTime());
        assertEquals(new BigDecimal("25.00"), result.getBalance());
        assertEquals(new BigDecimal("5.00"), result.getWithdrawn());
        assertEquals(DATE, result.getDate());
        assertEquals(DATE, result.getCreatedAt());
        assertEquals(DATE, result.getUpdatedAt());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((AffiliateDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate) null));
    }
}
