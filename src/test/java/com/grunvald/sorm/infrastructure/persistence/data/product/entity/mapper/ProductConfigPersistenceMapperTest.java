package com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.product.entity.ProductConfigDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.testutil.MapStructTestFactory.productConfigPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductConfigPersistenceMapperTest {

    private ProductConfigPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = productConfigPersistenceMapper();
    }

    @Test
    void shouldMapDbToModel() {
        ProductConfigDb db = new ProductConfigDb();
        db.setId(1L);
        db.setName("Cores");
        db.setType("qty");

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals("Cores", result.getName());
        assertEquals("qty", result.getType());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfig(1L, "Cores", "qty");

        ProductConfigDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals("Cores", result.getName());
        assertEquals("qty", result.getType());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((ProductConfigDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfig) null));
    }
}
