package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Price;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product;
import com.grunvald.sorm.service.whmcsprovider.dto.ProductProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapperImpl();

    private static ProductProviderDto.Price price(String currency, String suffix) {
        var price = new ProductProviderDto.Price();
        price.setCurrency(currency);
        price.setMSetupFee(new BigDecimal("1" + suffix));
        price.setQSetupFee(new BigDecimal("2" + suffix));
        price.setSSetupFee(new BigDecimal("3" + suffix));
        price.setASetupFee(new BigDecimal("4" + suffix));
        price.setBSetupFee(new BigDecimal("5" + suffix));
        price.setTSetupFee(new BigDecimal("6" + suffix));
        price.setMonthly(new BigDecimal("7" + suffix));
        price.setQuarterly(new BigDecimal("8" + suffix));
        price.setSemiannually(new BigDecimal("9" + suffix));
        price.setAnnually(new BigDecimal("10" + suffix));
        price.setBiennially(new BigDecimal("11" + suffix));
        price.setTriennially(new BigDecimal("12" + suffix));
        return price;
    }

    @Test
    void map_fullDto_mapsAllFieldsAndPricing() {
        var dto = ProductProviderDto.builder()
                .id(1L)
                .type("hosting")
                .payType("recurring")
                .name("Plan")
                .description("description")
                .prices(List.of(price("USD", ".0"), price("RUB", ".1")))
                .build();

        Product result = mapper.map(dto);

        assertEquals(1L, result.getId());
        assertEquals("hosting", result.getType());
        assertEquals("recurring", result.getPayType());
        assertEquals("Plan", result.getName());
        assertEquals("description", result.getDescription());
        assertNull(result.getUpdatedAt());
        assertFalse(result.isDeleted());

        assertEquals(2, result.getPrices().size());
        var usd = result.getPrices().stream()
                .filter(p -> "USD".equals(p.getCurrency()))
                .findFirst().orElseThrow();
        assertPriceFields(usd, ".0");
        var rub = result.getPrices().stream()
                .filter(p -> "RUB".equals(p.getCurrency()))
                .findFirst().orElseThrow();
        assertPriceFields(rub, ".1");
    }

    private static void assertPriceFields(Price price, String suffix) {
        assertEquals(new BigDecimal("1" + suffix), price.getMSetupFee());
        assertEquals(new BigDecimal("2" + suffix), price.getQSetupFee());
        assertEquals(new BigDecimal("3" + suffix), price.getSSetupFee());
        assertEquals(new BigDecimal("4" + suffix), price.getASetupFee());
        assertEquals(new BigDecimal("5" + suffix), price.getBSetupFee());
        assertEquals(new BigDecimal("6" + suffix), price.getTSetupFee());
        assertEquals(new BigDecimal("7" + suffix), price.getMonthly());
        assertEquals(new BigDecimal("8" + suffix), price.getQuarterly());
        assertEquals(new BigDecimal("9" + suffix), price.getSemiannually());
        assertEquals(new BigDecimal("10" + suffix), price.getAnnually());
        assertEquals(new BigDecimal("11" + suffix), price.getBiennially());
        assertEquals(new BigDecimal("12" + suffix), price.getTriennially());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
