package com.grunvald.sorm.infrastructure.persistence.data.client.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.client.entity.ClientGroupDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.clientGroupPersistenceMapper;
import static org.junit.jupiter.api.Assertions.*;

class ClientGroupPersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ClientGroupPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = clientGroupPersistenceMapper();
    }

    @Test
    void shouldMapDbToModel() {
        ClientGroupDb db = new ClientGroupDb();
        db.setId(1L);
        db.setName("Premium");
        db.setDiscountPercent(new BigDecimal("7.50"));
        db.setUpdatedAt(DATE);
        db.setDeleted(true);

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals("Premium", result.getName());
        assertEquals(new BigDecimal("7.50"), result.getDiscountPercent());
        assertEquals(DATE, result.getUpdatedAt());
        assertTrue(result.isDeleted());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup("Premium", new BigDecimal("7.50"));
        model.setId(1L);
        model.setUpdatedAt(DATE);
        model.setDeleted(true);

        ClientGroupDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals("Premium", result.getName());
        assertEquals(new BigDecimal("7.50"), result.getDiscountPercent());
        assertEquals(DATE, result.getUpdatedAt());
        assertTrue(result.isDeleted());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((ClientGroupDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup) null));
    }
}
