package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientProductProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientProductMapperTest {

    private final ClientProductMapper mapper = new ClientProductMapperImpl();

    @Test
    void map_fullDto_mapsAllFields() {
        var configOption = new ClientProductProviderDto.ConfigOption();
        configOption.setId(9L);
        configOption.setOption("optionName");
        configOption.setType("typeName");
        configOption.setValue("valueName");

        var dto = new ClientProductProviderDto();
        dto.setId(1L);
        dto.setClientId(2L);
        dto.setOrderId(3L);
        dto.setPid(5L);
        dto.setName("Plan");
        dto.setDomain("example.com");
        dto.setDedicatedIp("1.2.3.4");
        dto.setFirstPaymentAmount(new BigDecimal("10.5"));
        dto.setRecurringAmount(new BigDecimal("5.5"));
        dto.setBillingCycle("Monthly");
        dto.setStatus("Active");
        dto.setAssignedIps(List.of("1.2.3.4", "5.6.7.8"));
        dto.setRegDate(OffsetDateTime.parse("2026-01-02T03:04:05Z"));
        dto.setNextDueDate(OffsetDateTime.parse("2026-02-02T03:04:05Z"));
        dto.setLastUpdate(OffsetDateTime.parse("2026-03-02T03:04:05Z"));
        dto.setConfigOptions(List.of(configOption));

        ClientProduct result = mapper.map(dto);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(5L, result.getProductId());
        assertEquals(3L, result.getOrderId());
        assertEquals("Plan", result.getName());
        assertEquals("example.com", result.getDomain());
        assertEquals("1.2.3.4", result.getDedicatedIpAddress());
        assertEquals(new BigDecimal("10.5"), result.getFirstPaymentAmount());
        assertEquals(new BigDecimal("5.5"), result.getRecurringAmount());
        assertEquals("Monthly", result.getBillingCycle());
        assertEquals("Active", result.getStatus());
        assertEquals(List.of("1.2.3.4", "5.6.7.8"), result.getAssignedIpAddresses());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getRegDate());
        assertEquals(OffsetDateTime.parse("2026-02-02T03:04:05Z"), result.getNextDueDate());
        assertEquals(OffsetDateTime.parse("2026-03-02T03:04:05Z"), result.getLastUpdate());

        assertEquals(1, result.getConfigs().size());
        var config = result.getConfigs().getFirst();
        assertEquals(9L, config.getId());
        assertEquals("optionName", config.getOption());
        assertEquals("typeName", config.getType());
        assertEquals("valueName", config.getValue());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
