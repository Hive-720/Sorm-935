package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Price;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Product;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.productPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductPresentationMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ProductPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = productPresentationMapper();
    }

    @Test
    void shouldMapAllFields() {
        var price = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Price();
        price.setCurrency("RUB");
        price.setMSetupFee(new BigDecimal("1.00"));
        price.setMonthly(new BigDecimal("100.00"));

        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product();
        source.setId(1L);
        source.setType("vps");
        source.setName("VPS-1");
        source.setDescription("Virtual server");
        source.setPayType("recurring");
        source.setPrices(List.of(price));
        source.setUpdatedAt(DATE);
        source.setDeleted(true);

        Product result = mapper.map(source);

        assertEquals(1L, result.id());
        assertEquals("vps", result.type());
        assertEquals("VPS-1", result.name());
        assertEquals("Virtual server", result.description());
        assertEquals("recurring", result.payType());
        assertEquals(1, result.prices().size());
        Price mappedPrice = result.prices().get(0);
        assertEquals("RUB", mappedPrice.currency());
        assertEquals(new BigDecimal("1.00"), mappedPrice.mSetupFee());
        assertEquals(new BigDecimal("100.00"), mappedPrice.monthly());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.updatedAt());
        assertEquals(true, result.deleted());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
