package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.config.Config;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientsResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientShortProviderDto;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientShortProviderMapperTest {

    private static final ObjectMapper OBJECT_MAPPER = new Config().objectMapper();

    private final ClientShortProviderMapper mapper = new ClientShortProviderMapperImpl();

    private static <T> T fromJson(String json, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Failed to parse fixture json", e);
        }
    }

    @Test
    void map_fullClient_mapsAllFields() {
        var source = fromJson("""
                {
                  "id": 1,
                  "firstname": "Ivan",
                  "lastname": "Petrov",
                  "companyname": "ACME",
                  "email": "ivan@example.com",
                  "datecreated": "2026-01-02 03:04:05",
                  "groupid": 7,
                  "status": "Active"
                }
                """, GetClientsResponse.Clients.Client.class);

        ClientShortProviderDto result = mapper.map(source);

        assertEquals(1L, result.getId());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Petrov", result.getLastName());
        assertEquals("ACME", result.getCompanyName());
        assertEquals("ivan@example.com", result.getEmail());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDateCreated());
        assertEquals(7L, result.getGroupId());
        assertEquals("Active", result.getStatus());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
