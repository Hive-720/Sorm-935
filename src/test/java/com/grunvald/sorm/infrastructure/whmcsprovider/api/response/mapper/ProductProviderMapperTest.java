package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.config.Config;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetProductsResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.ProductProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductProviderMapperTest {

    private static final ObjectMapper OBJECT_MAPPER = new Config().objectMapper();

    private final ProductProviderMapper mapper = new ProductProviderMapperImpl();

    private static <T> T fromJson(String json, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Failed to parse fixture json", e);
        }
    }

    private static void assertAllFeeFields(ProductProviderDto.Price price,
                                           BigDecimal m, BigDecimal q, BigDecimal s,
                                           BigDecimal a, BigDecimal b, BigDecimal t,
                                           BigDecimal monthly, BigDecimal quarterly,
                                           BigDecimal semiannually, BigDecimal annually,
                                           BigDecimal biennially, BigDecimal triennially) {
        assertEquals(m, price.getMSetupFee());
        assertEquals(q, price.getQSetupFee());
        assertEquals(s, price.getSSetupFee());
        assertEquals(a, price.getASetupFee());
        assertEquals(b, price.getBSetupFee());
        assertEquals(t, price.getTSetupFee());
        assertEquals(monthly, price.getMonthly());
        assertEquals(quarterly, price.getQuarterly());
        assertEquals(semiannually, price.getSemiannually());
        assertEquals(annually, price.getAnnually());
        assertEquals(biennially, price.getBiennially());
        assertEquals(triennially, price.getTriennially());
    }

    private static ProductProviderDto.Price findPrice(List<ProductProviderDto.Price> prices, String currency) {
        return prices.stream()
                .filter(p -> currency.equals(p.getCurrency()))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void map_fullProduct_mapsAllFieldsAndPricing() {
        var source = fromJson("""
                {
                  "pid": 1,
                  "gid": 2,
                  "type": "hosting",
                  "name": "Plan",
                  "slug": "plan",
                  "product_url": "url",
                  "description": "description",
                  "module": "module",
                  "paytype": "recurring",
                  "pricing": {
                    "USD": {
                      "msetupfee": 1.0, "qsetupfee": 2.0, "ssetupfee": 3.0,
                      "asetupfee": 4.0, "bsetupfee": 5.0, "tsetupfee": 6.0,
                      "monthly": 7.0, "quarterly": 8.0, "semiannually": 9.0,
                      "annually": 10.0, "biennially": 11.0, "triennially": 12.0
                    },
                    "RUB": {
                      "msetupfee": 1.1, "qsetupfee": 2.1, "ssetupfee": 3.1,
                      "asetupfee": 4.1, "bsetupfee": 5.1, "tsetupfee": 6.1,
                      "monthly": 7.1, "quarterly": 8.1, "semiannually": 9.1,
                      "annually": 10.1, "biennially": 11.1, "triennially": 12.1
                    }
                  },
                  "configoptions": {
                    "configoption": [
                      {
                        "id": 1,
                        "name": "cfgName",
                        "type": "cfgType",
                        "options": {
                          "option": [
                            {
                              "id": 2,
                              "name": "optName",
                              "pricing": {
                                "EUR": {
                                  "msetupfee": 1.2, "qsetupfee": 2.2, "ssetupfee": 3.2,
                                  "asetupfee": 4.2, "bsetupfee": 5.2, "tsetupfee": 6.2,
                                  "monthly": 7.2, "quarterly": 8.2, "semiannually": 9.2,
                                  "annually": 10.2, "biennially": 11.2, "triennially": 12.2
                                }
                              }
                            }
                          ]
                        }
                      }
                    ]
                  }
                }
                """, GetProductsResponse.Products.Product.class);

        ProductProviderDto result = mapper.map(source);

        assertEquals(1L, result.getId());
        assertEquals("hosting", result.getType());
        assertEquals("Plan", result.getName());
        assertEquals("description", result.getDescription());
        assertEquals("recurring", result.getPayType());

        assertEquals(2, result.getPrices().size());
        assertAllFeeFields(findPrice(result.getPrices(), "USD"),
                new BigDecimal("1.0"), new BigDecimal("2.0"), new BigDecimal("3.0"),
                new BigDecimal("4.0"), new BigDecimal("5.0"), new BigDecimal("6.0"),
                new BigDecimal("7.0"), new BigDecimal("8.0"), new BigDecimal("9.0"),
                new BigDecimal("10.0"), new BigDecimal("11.0"), new BigDecimal("12.0"));
        assertAllFeeFields(findPrice(result.getPrices(), "RUB"),
                new BigDecimal("1.1"), new BigDecimal("2.1"), new BigDecimal("3.1"),
                new BigDecimal("4.1"), new BigDecimal("5.1"), new BigDecimal("6.1"),
                new BigDecimal("7.1"), new BigDecimal("8.1"), new BigDecimal("9.1"),
                new BigDecimal("10.1"), new BigDecimal("11.1"), new BigDecimal("12.1"));

        assertEquals(1, result.getConfigs().size());
        var config = result.getConfigs().get(0);
        assertEquals(1L, config.getId());
        assertEquals("cfgName", config.getName());
        assertEquals("cfgType", config.getType());

        assertEquals(1, config.getOptions().size());
        var option = config.getOptions().get(0);
        assertEquals(2, option.getId());
        assertEquals("optName", option.getName());
        assertEquals(1, option.getPrices().size());
        assertAllFeeFields(findPrice(option.getPrices(), "EUR"),
                new BigDecimal("1.2"), new BigDecimal("2.2"), new BigDecimal("3.2"),
                new BigDecimal("4.2"), new BigDecimal("5.2"), new BigDecimal("6.2"),
                new BigDecimal("7.2"), new BigDecimal("8.2"), new BigDecimal("9.2"),
                new BigDecimal("10.2"), new BigDecimal("11.2"), new BigDecimal("12.2"));
    }

    @Test
    void map_productWithoutPricing_mapsNullPrices() {
        var source = fromJson("{\"pid\": 1, \"name\": \"Plan\"}", GetProductsResponse.Products.Product.class);

        ProductProviderDto result = mapper.map(source);

        assertNull(result.getPrices());
    }

    @Test
    void map_productWithEmptyPricing_mapsEmptyPrices() {
        var source = fromJson("{\"pid\": 1, \"pricing\": {}}", GetProductsResponse.Products.Product.class);

        ProductProviderDto result = mapper.map(source);

        assertNotNull(result.getPrices());
        assertEquals(0, result.getPrices().size());
    }

    @Test
    void optionToOption_fullOption_mapsAllFields() {
        var source = fromJson("""
                {
                  "id": 2,
                  "name": "optName",
                  "pricing": {
                    "EUR": {
                      "msetupfee": 1.2, "qsetupfee": 2.2, "ssetupfee": 3.2,
                      "asetupfee": 4.2, "bsetupfee": 5.2, "tsetupfee": 6.2,
                      "monthly": 7.2, "quarterly": 8.2, "semiannually": 9.2,
                      "annually": 10.2, "biennially": 11.2, "triennially": 12.2
                    }
                  }
                }
                """, GetProductsResponse.Products.Product.ProductConfig.Config.Options.Option.class);

        ProductProviderDto.Config.Option result = mapper.optionToOption(source);

        assertEquals(2, result.getId());
        assertEquals("optName", result.getName());
        assertEquals(1, result.getPrices().size());
        assertAllFeeFields(findPrice(result.getPrices(), "EUR"),
                new BigDecimal("1.2"), new BigDecimal("2.2"), new BigDecimal("3.2"),
                new BigDecimal("4.2"), new BigDecimal("5.2"), new BigDecimal("6.2"),
                new BigDecimal("7.2"), new BigDecimal("8.2"), new BigDecimal("9.2"),
                new BigDecimal("10.2"), new BigDecimal("11.2"), new BigDecimal("12.2"));
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }

    @Test
    void optionToOption_null_returnsNull() {
        assertNull(mapper.optionToOption(null));
    }
}
