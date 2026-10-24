package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ProductConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.testutil.MapStructTestFactory.productConfigPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductConfigPresentationMapperTest {

    private ProductConfigPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = productConfigPresentationMapper();
    }

    @Test
    void shouldMapAllFields() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfig(1L, "Cores", "qty");

        ProductConfig result = mapper.map(source);

        assertEquals(1L, result.id());
        assertEquals("Cores", result.name());
        assertEquals("qty", result.type());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
