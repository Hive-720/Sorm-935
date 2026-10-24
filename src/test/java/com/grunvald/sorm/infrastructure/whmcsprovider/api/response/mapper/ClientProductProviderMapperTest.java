package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.config.Config;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientProductsResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientProductProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientProductProviderMapperTest {

    private static final ObjectMapper OBJECT_MAPPER = new Config().objectMapper();

    private final ClientProductProviderMapper mapper = new ClientProductProviderMapperImpl();

    private static <T> T fromJson(String json, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Failed to parse fixture json", e);
        }
    }

    @Test
    void map_fullProduct_mapsAllFields() {
        var source = fromJson("""
                {
                  "id": 1,
                  "clientid": 2,
                  "orderid": 3,
                  "ordernumber": 4,
                  "pid": 5,
                  "regdate": "2026-01-02 03:04:05",
                  "name": "Plan",
                  "domain": "example.com",
                  "dedicatedip": "1.2.3.4",
                  "firstpaymentamount": 10.5,
                  "recurringamount": 5.5,
                  "billingcycle": "Monthly",
                  "nextduedate": "2026-02-02 03:04:05",
                  "status": "Active",
                  "assignedips": "1.2.3.4\\n5.6.7.8",
                  "lastupdate": "2026-03-02 03:04:05",
                  "configoptions": {
                    "configoption": [
                      {"id": 9, "option": "optionName", "type": "typeName", "value": "valueName"}
                    ]
                  }
                }
                """, GetClientProductsResponse.Products.Product.class);

        ClientProductProviderDto result = mapper.map(source);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(3L, result.getOrderId());
        assertEquals(5L, result.getPid());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getRegDate());
        assertEquals("Plan", result.getName());
        assertEquals("example.com", result.getDomain());
        assertEquals("1.2.3.4", result.getDedicatedIp());
        assertEquals(new BigDecimal("10.5"), result.getFirstPaymentAmount());
        assertEquals(new BigDecimal("5.5"), result.getRecurringAmount());
        assertEquals("Monthly", result.getBillingCycle());
        assertEquals(OffsetDateTime.parse("2026-02-02T03:04:05Z"), result.getNextDueDate());
        assertEquals("Active", result.getStatus());
        assertEquals(List.of("1.2.3.4", "5.6.7.8"), result.getAssignedIps());
        assertEquals(OffsetDateTime.parse("2026-03-02T03:04:05Z"), result.getLastUpdate());

        assertEquals(1, result.getConfigOptions().size());
        var configOption = result.getConfigOptions().get(0);
        assertEquals(9L, configOption.getId());
        assertEquals("optionName", configOption.getOption());
        assertEquals("typeName", configOption.getType());
        assertEquals("valueName", configOption.getValue());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
