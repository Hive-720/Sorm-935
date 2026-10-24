package com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.ProductConfigOptionDb;
import com.grunvald.sorm.service.exception.InternalErrorException;
import io.r2dbc.postgresql.codec.Json;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.productConfigOptionPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductConfigOptionPersistenceMapperTest {

    private ProductConfigOptionPersistenceMapper mapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mapper = productConfigOptionPersistenceMapper();
        objectMapper = new ObjectMapper();
    }

    private List<com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Price> prices() {
        var price = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Price();
        price.setCurrency("RUB");
        price.setMonthly(new BigDecimal("100.00"));
        price.setAnnually(new BigDecimal("1000.00"));
        return List.of(price);
    }

    @Test
    void shouldMapDbToModelWithJsonPrices() throws Exception {
        ProductConfigOptionDb db = new ProductConfigOptionDb();
        db.setId(1L);
        db.setProductConfigId(2L);
        db.setName("Cores");
        db.setPrices(Json.of(objectMapper.writeValueAsString(prices())));

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getProductConfigId());
        assertEquals("Cores", result.getName());
        assertEquals(1, result.getPrices().size());
        assertEquals("RUB", result.getPrices().get(0).getCurrency());
        assertEquals(new BigDecimal("100.00"), result.getPrices().get(0).getMonthly());
        assertEquals(new BigDecimal("1000.00"), result.getPrices().get(0).getAnnually());
    }

    @Test
    void shouldMapModelToDbWithJsonPrices() throws Exception {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption();
        model.setId(1L);
        model.setProductConfigId(2L);
        model.setName("Cores");
        model.setPrices(prices());

        ProductConfigOptionDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getProductConfigId());
        assertEquals("Cores", result.getName());
        assertEquals(objectMapper.writeValueAsString(prices()), result.getPrices().asString());
    }

    @Test
    void shouldMapNullJsonToNullPrices() {
        ProductConfigOptionDb db = new ProductConfigOptionDb();
        db.setPrices(null);

        var result = mapper.map(db);

        assertNull(result.getPrices());
    }

    @Test
    void shouldMapNullPricesToNullJson() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption();
        model.setPrices(null);

        ProductConfigOptionDb result = mapper.map(model);

        assertNull(result.getPrices());
    }

    @Test
    void shouldMapEmptyPricesToNullJson() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption();
        model.setPrices(List.of());

        ProductConfigOptionDb result = mapper.map(model);

        assertNull(result.getPrices());
    }

    @Test
    void shouldThrowInternalErrorExceptionOnInvalidJson() {
        ProductConfigOptionDb db = new ProductConfigOptionDb();
        db.setPrices(Json.of("невалидный json"));

        assertThrows(InternalErrorException.class, () -> mapper.map(db));
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((ProductConfigOptionDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption) null));
    }
}
