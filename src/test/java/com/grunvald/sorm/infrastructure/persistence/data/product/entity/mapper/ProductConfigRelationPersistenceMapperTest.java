package com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.product.entity.relation.ProductConfigRelationDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.productConfigRelationPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductConfigRelationPersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ProductConfigRelationPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = productConfigRelationPersistenceMapper();
    }

    @Test
    void shouldMapDbToModel() {
        ProductConfigRelationDb db = new ProductConfigRelationDb();
        db.setProductId(1L);
        db.setProductConfigId(2L);
        db.setCreatedAt(DATE);

        var result = mapper.map(db);

        assertEquals(1L, result.getProductId());
        assertEquals(2L, result.getProductConfigId());
        assertEquals(DATE, result.getCreatedAt());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigRelation(1L, 2L, DATE);

        ProductConfigRelationDb result = mapper.map(model);

        assertEquals(1L, result.getProductId());
        assertEquals(2L, result.getProductConfigId());
        assertEquals(DATE, result.getCreatedAt());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((ProductConfigRelationDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigRelation) null));
    }
}
