package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ProductResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.dto.ProductSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.productResultMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductResultMapperTest {

    private ProductResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = productResultMapper();
    }

    @Test
    void shouldMapSearchResultToResult() {
        var first = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product();
        first.setId(1L);
        first.setName("VPS-1");
        var second = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product();
        second.setId(2L);
        second.setName("VPS-2");

        ProductResult result = mapper.map(new ProductSearchResult("cursor-1", true, List.of(first, second)));

        assertEquals("cursor-1", result.cursor());
        assertEquals(true, result.hasNextPage());
        assertEquals(2, result.result().size());
        assertEquals(1L, result.result().get(0).id());
        assertEquals("VPS-1", result.result().get(0).name());
        assertEquals(2L, result.result().get(1).id());
        assertEquals("VPS-2", result.result().get(1).name());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
