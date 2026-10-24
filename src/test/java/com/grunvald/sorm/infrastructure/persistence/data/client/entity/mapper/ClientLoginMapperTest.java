package com.grunvald.sorm.infrastructure.persistence.data.client.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.client.entity.ClientLoginDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.clientLoginMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientLoginMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ClientLoginMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = clientLoginMapper();
    }

    @Test
    void shouldMapDbToModel() {
        ClientLoginDb db = new ClientLoginDb();
        db.setId(1L);
        db.setClientId(2L);
        db.setIpAddress("10.0.0.1");
        db.setDate(DATE);

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals("10.0.0.1", result.getIpAddress());
        assertEquals(DATE, result.getDate());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin(2L, "10.0.0.1", DATE);
        model.setId(1L);

        ClientLoginDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals("10.0.0.1", result.getIpAddress());
        assertEquals(DATE, result.getDate());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((ClientLoginDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin) null));
    }
}
