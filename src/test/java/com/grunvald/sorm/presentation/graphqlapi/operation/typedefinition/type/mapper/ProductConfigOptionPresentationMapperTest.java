package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Price;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ProductConfigOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.productConfigOptionPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductConfigOptionPresentationMapperTest {

    private ProductConfigOptionPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = productConfigOptionPresentationMapper();
    }

    @Test
    void shouldMapAllFields() {
        var price = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Price();
        price.setCurrency("RUB");
        price.setMonthly(new BigDecimal("100.00"));

        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption();
        source.setId(1L);
        source.setProductConfigId(2L);
        source.setName("Cores");
        source.setPrices(List.of(price));

        ProductConfigOption result = mapper.map(source);

        assertEquals(1L, result.id());
        assertEquals(2L, result.productConfigId());
        assertEquals("Cores", result.name());
        assertEquals(1, result.prices().size());
        Price mappedPrice = result.prices().get(0);
        assertEquals("RUB", mappedPrice.currency());
        assertEquals(new BigDecimal("100.00"), mappedPrice.monthly());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
