package com.grunvald.sorm.infrastructure.persistence.data.client.entity.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.infrastructure.persistence.data.client.entity.ClientProductDb;
import com.grunvald.sorm.service.exception.InternalErrorException;
import io.r2dbc.postgresql.codec.Json;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.clientProductPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClientProductPersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ClientProductPersistenceMapper mapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mapper = clientProductPersistenceMapper();
        objectMapper = new ObjectMapper();
    }

    private List<com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct.ConfigValue> configs() {
        var config = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct.ConfigValue();
        config.setId(1L);
        config.setOption("cores");
        config.setType("qty");
        config.setValue("4");
        return List.of(config);
    }

    @Test
    void shouldMapDbToModelWithJsonConfigsAndIps() throws Exception {
        ClientProductDb db = new ClientProductDb();
        db.setId(1L);
        db.setClientId(2L);
        db.setProductId(3L);
        db.setOrderId(4L);
        db.setName("VPS");
        db.setDomain("example.com");
        db.setDedicatedIpAddress("10.0.0.1");
        db.setFirstPaymentAmount(new BigDecimal("100.00"));
        db.setRecurringAmount(new BigDecimal("50.00"));
        db.setBillingCycle("Monthly");
        db.setAssignedIps(new String[]{"10.0.0.2", "10.0.0.3"});
        db.setStatus("Active");
        db.setConfigs(Json.of(objectMapper.writeValueAsString(configs())));
        db.setRegDate(DATE);
        db.setNextDueDate(DATE);
        db.setLastUpdate(DATE);

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(3L, result.getProductId());
        assertEquals(4L, result.getOrderId());
        assertEquals("VPS", result.getName());
        assertEquals("example.com", result.getDomain());
        assertEquals("10.0.0.1", result.getDedicatedIpAddress());
        assertEquals(new BigDecimal("100.00"), result.getFirstPaymentAmount());
        assertEquals(new BigDecimal("50.00"), result.getRecurringAmount());
        assertEquals("Monthly", result.getBillingCycle());
        assertEquals(List.of("10.0.0.2", "10.0.0.3"), result.getAssignedIpAddresses());
        assertEquals("Active", result.getStatus());
        assertEquals(1, result.getConfigs().size());
        assertEquals(1L, result.getConfigs().getFirst().getId());
        assertEquals("cores", result.getConfigs().getFirst().getOption());
        assertEquals("qty", result.getConfigs().getFirst().getType());
        assertEquals("4", result.getConfigs().getFirst().getValue());
        assertEquals(DATE, result.getRegDate());
        assertEquals(DATE, result.getNextDueDate());
        assertEquals(DATE, result.getLastUpdate());
    }

    @Test
    void shouldMapModelToDbWithJsonConfigsAndIps() throws Exception {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct();
        model.setId(1L);
        model.setClientId(2L);
        model.setProductId(3L);
        model.setOrderId(4L);
        model.setName("VPS");
        model.setDomain("example.com");
        model.setDedicatedIpAddress("10.0.0.1");
        model.setFirstPaymentAmount(new BigDecimal("100.00"));
        model.setRecurringAmount(new BigDecimal("50.00"));
        model.setBillingCycle("Monthly");
        model.setAssignedIpAddresses(List.of("10.0.0.2", "10.0.0.3"));
        model.setStatus("Active");
        model.setConfigs(configs());
        model.setRegDate(DATE);
        model.setNextDueDate(DATE);
        model.setLastUpdate(DATE);

        ClientProductDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(3L, result.getProductId());
        assertEquals(4L, result.getOrderId());
        assertEquals("VPS", result.getName());
        assertEquals("example.com", result.getDomain());
        assertEquals("10.0.0.1", result.getDedicatedIpAddress());
        assertEquals(new BigDecimal("100.00"), result.getFirstPaymentAmount());
        assertEquals(new BigDecimal("50.00"), result.getRecurringAmount());
        assertEquals("Monthly", result.getBillingCycle());
        assertArrayEquals(new String[]{"10.0.0.2", "10.0.0.3"}, result.getAssignedIps());
        assertEquals("Active", result.getStatus());
        assertEquals(objectMapper.writeValueAsString(configs()), result.getConfigs().asString());
        assertEquals(DATE, result.getRegDate());
        assertEquals(DATE, result.getNextDueDate());
        assertEquals(DATE, result.getLastUpdate());
    }

    @Test
    void shouldMapNullJsonToNullConfigs() {
        ClientProductDb db = new ClientProductDb();
        db.setConfigs(null);
        db.setAssignedIps(null);

        var result = mapper.map(db);

        assertNull(result.getConfigs());
        assertNull(result.getAssignedIpAddresses());
    }

    @Test
    void shouldMapNullConfigsToNullJson() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct();
        model.setConfigs(null);
        model.setAssignedIpAddresses(null);

        ClientProductDb result = mapper.map(model);

        assertNull(result.getConfigs());
        assertNull(result.getAssignedIps());
    }

    @Test
    void shouldThrowInternalErrorExceptionOnInvalidJson() {
        ClientProductDb db = new ClientProductDb();
        db.setConfigs(Json.of("невалидный json"));

        assertThrows(InternalErrorException.class, () -> mapper.map(db));
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((ClientProductDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct) null));
    }
}
