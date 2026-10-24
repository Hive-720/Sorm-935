package com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.ProductDb;
import com.grunvald.sorm.service.exception.InternalErrorException;
import io.r2dbc.postgresql.codec.Json;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.productPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductPersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ProductPersistenceMapper mapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mapper = productPersistenceMapper();
        objectMapper = new ObjectMapper();
    }

    private List<com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Price> prices() {
        var price = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Price();
        price.setCurrency("RUB");
        price.setMSetupFee(new BigDecimal("1.00"));
        price.setMonthly(new BigDecimal("100.00"));
        price.setQuarterly(new BigDecimal("280.00"));
        return List.of(price);
    }

    @Test
    void shouldMapDbToModelWithJsonPrices() throws Exception {
        ProductDb db = new ProductDb();
        db.setId(1L);
        db.setType("vps");
        db.setName("VPS-1");
        db.setDescription("Virtual server");
        db.setPayType("recurring");
        db.setPrices(Json.of(objectMapper.writeValueAsString(prices())));
        db.setUpdatedAt(DATE);
        db.setDeleted(true);

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals("vps", result.getType());
        assertEquals("VPS-1", result.getName());
        assertEquals("Virtual server", result.getDescription());
        assertEquals("recurring", result.getPayType());
        assertEquals(1, result.getPrices().size());
        assertEquals("RUB", result.getPrices().get(0).getCurrency());
        assertEquals(new BigDecimal("1.00"), result.getPrices().get(0).getMSetupFee());
        assertEquals(new BigDecimal("100.00"), result.getPrices().get(0).getMonthly());
        assertEquals(new BigDecimal("280.00"), result.getPrices().get(0).getQuarterly());
        assertEquals(DATE, result.getUpdatedAt());
        assertEquals(true, result.isDeleted());
    }

    @Test
    void shouldMapModelToDbWithJsonPrices() throws Exception {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product();
        model.setId(1L);
        model.setType("vps");
        model.setName("VPS-1");
        model.setDescription("Virtual server");
        model.setPayType("recurring");
        model.setPrices(prices());
        model.setUpdatedAt(DATE);
        model.setDeleted(true);

        ProductDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals("vps", result.getType());
        assertEquals("VPS-1", result.getName());
        assertEquals("Virtual server", result.getDescription());
        assertEquals("recurring", result.getPayType());
        assertEquals(objectMapper.writeValueAsString(prices()), result.getPrices().asString());
        assertEquals(DATE, result.getUpdatedAt());
        assertEquals(true, result.isDeleted());
    }

    @Test
    void shouldMapNullJsonToNullPrices() {
        ProductDb db = new ProductDb();
        db.setPrices(null);

        var result = mapper.map(db);

        assertNull(result.getPrices());
    }

    @Test
    void shouldMapNullPricesToNullJson() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product();
        model.setPrices(null);

        ProductDb result = mapper.map(model);

        assertNull(result.getPrices());
    }

    @Test
    void shouldMapEmptyPricesToNullJson() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product();
        model.setPrices(List.of());

        ProductDb result = mapper.map(model);

        assertNull(result.getPrices());
    }

    @Test
    void shouldThrowInternalErrorExceptionOnInvalidJson() {
        ProductDb db = new ProductDb();
        db.setPrices(Json.of("невалидный json"));

        assertThrows(InternalErrorException.class, () -> mapper.map(db));
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((ProductDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product) null));
    }
}
