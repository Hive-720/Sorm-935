package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.config.Config;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetUsersResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.UserProviderDto;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserProviderMapperTest {

    private static final ObjectMapper OBJECT_MAPPER = new Config().objectMapper();

    private final UserProviderMapper mapper = new UserProviderMapperImpl();

    private static <T> T fromJson(String json, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Failed to parse fixture json", e);
        }
    }

    @Test
    void map_fullUser_mapsAllFields() {
        var source = fromJson("""
                {
                  "id": 1,
                  "firstname": "Ivan",
                  "lastname": "Petrov",
                  "email": "ivan@example.com",
                  "datecreated": "2026-01-02 03:04:05",
                  "clients": [{"id": 9}, {"id": 10}]
                }
                """, GetUsersResponse.User.class);

        UserProviderDto result = mapper.map(source);

        assertEquals(1L, result.getId());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Petrov", result.getLastName());
        assertEquals("ivan@example.com", result.getEmail());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDateCreated());

        assertEquals(2, result.getClients().size());
        assertEquals(9L, result.getClients().get(0).getId());
        assertEquals(10L, result.getClients().get(1).getId());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
