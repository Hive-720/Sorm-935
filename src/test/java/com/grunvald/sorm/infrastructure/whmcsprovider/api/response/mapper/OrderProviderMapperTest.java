package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.config.Config;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetOrdersResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.OrderProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderProviderMapperTest {

    private static final ObjectMapper OBJECT_MAPPER = new Config().objectMapper();

    private final OrderProviderMapper mapper = new OrderProviderMapperImpl();

    private static <T> T fromJson(String json, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Failed to parse fixture json", e);
        }
    }

    @Test
    void map_fullOrder_mapsAllFields() {
        var source = fromJson("""
                {
                  "id": 1,
                  "ordernum": 100,
                  "userid": 2,
                  "contactid": 3,
                  "date": "2026-01-02 03:04:05",
                  "amount": 12.5,
                  "paymentmethod": "paypal",
                  "invoiceid": 9,
                  "status": "Pending",
                  "ipaddress": "1.2.3.4",
                  "paymentmethodname": "PayPal",
                  "paymentstatus": "Paid",
                  "currencyprefix": " USD ",
                  "currencysuffix": " EUR ",
                  "lineitems": {
                    "lineitem": [
                      {
                        "type": "product",
                        "relid": 1,
                        "producttype": "hosting",
                        "product": "Plan X",
                        "domain": "example.com",
                        "billingcycle": "12",
                        "amount": "10.5",
                        "status": "s"
                      }
                    ]
                  }
                }
                """, GetOrdersResponse.Orders.Order.class);

        OrderProviderDto result = mapper.map(source);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(3L, result.getContactId());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDate());
        assertEquals(new BigDecimal("12.5"), result.getAmount());
        assertEquals("paypal", result.getPaymentMethod());
        assertEquals(9L, result.getInvoiceId());
        assertEquals("Pending", result.getStatus());
        assertEquals("1.2.3.4", result.getIpAddress());
        assertEquals("PayPal", result.getPaymentMethodName());
        assertEquals("Paid", result.getPaymentStatus());
        assertEquals("USD", result.getCurrency());

        assertEquals(1, result.getLineItems().size());
        var lineItem = result.getLineItems().get(0);
        assertEquals("product", lineItem.getType());
        assertEquals(1L, lineItem.getRelId());
        assertEquals("hosting", lineItem.getProductType());
        assertEquals("Plan X", lineItem.getProduct());
        assertEquals("example.com", lineItem.getDomain());
        assertEquals(new BigDecimal("12"), lineItem.getBillingCycle());
        assertEquals(new BigDecimal("10.5"), lineItem.getAmount());
        assertEquals("s", lineItem.getStatus());
    }

    @Test
    void buildCurrency_invalidPrefix_usesSuffix() {
        var source = fromJson("{\"currencyprefix\": \"ZZZ\", \"currencysuffix\": \" EUR \"}",
                GetOrdersResponse.Orders.Order.class);

        OrderProviderDto result = mapper.map(source);

        assertEquals("EUR", result.getCurrency());
    }

    @Test
    void buildCurrency_bothInvalid_returnsNull() {
        var source = fromJson("{\"currencyprefix\": \"ZZZ\", \"currencysuffix\": \"ZZZ\"}",
                GetOrdersResponse.Orders.Order.class);

        OrderProviderDto result = mapper.map(source);

        assertNull(result.getCurrency());
    }

    @Test
    void buildCurrency_bothMissing_returnsNull() {
        var source = fromJson("{\"id\": 1}", GetOrdersResponse.Orders.Order.class);

        OrderProviderDto result = mapper.map(source);

        assertNull(result.getCurrency());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
