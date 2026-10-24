package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ClientProduct;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ConfigValue;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DomainNameBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpAddressBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.clientProductPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientProductPresentationMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ClientProductPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = clientProductPresentationMapper();
    }

    @Test
    void shouldMapAllFields() {
        var config1 = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct.ConfigValue();
        config1.setId(1L);
        config1.setOption("cores");
        config1.setType("qty");
        config1.setValue("4");
        var config2 = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct.ConfigValue();
        config2.setId(2L);
        config2.setOption("ram");
        config2.setType("qty");
        config2.setValue("8");

        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct();
        source.setId(77L);
        source.setClientId(5L);
        source.setProductId(6L);
        source.setOrderId(7L);
        source.setName("VPS");
        source.setDomain("example.com");
        source.setDedicatedIpAddress("10.0.0.1");
        source.setFirstPaymentAmount(new BigDecimal("100.00"));
        source.setRecurringAmount(new BigDecimal("50.00"));
        source.setBillingCycle("Monthly");
        source.setStatus("Active");
        source.setAssignedIpAddresses(List.of("10.0.0.2", "2001:db8::2"));
        source.setConfigs(List.of(config1, config2));
        source.setRegDate(DATE);
        source.setNextDueDate(DATE);
        source.setLastUpdate(DATE);

        ClientProduct result = mapper.map(source);

        assertEquals("77", result.id());
        assertEquals(5L, result.clientId());
        assertEquals(7L, result.orderId());
        assertEquals(6L, result.productId());
        assertEquals("VPS", result.name());
        assertEquals(new DomainNameBase("example.com"), result.domain());
        assertEquals(new IpAddressBase("10.0.0.1", IpType.IPV4), result.dedicatedIpAddress());
        assertEquals(new BigDecimal("100.00"), result.firstPaymentAmount());
        assertEquals(new BigDecimal("50.00"), result.recurringAmount());
        assertEquals("Monthly", result.billingCycle());
        assertEquals("Active", result.status());
        assertEquals(List.of(
                new IpAddressBase("10.0.0.2", IpType.IPV4),
                new IpAddressBase("2001:db8::2", IpType.IPV6)), result.assignedIpAddresses());
        assertEquals(List.of(
                new ConfigValue(1L, "cores", "qty", "4"),
                new ConfigValue(2L, "ram", "qty", "8")), result.configs());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.regDate());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.nextDueDate());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.lastUpdate());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
