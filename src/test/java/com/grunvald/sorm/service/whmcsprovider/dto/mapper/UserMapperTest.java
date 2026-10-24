package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.user.model.User;
import com.grunvald.sorm.service.whmcsprovider.dto.UserProviderDto;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    private final UserMapper mapper = new UserMapperImpl();

    private static UserProviderDto fullDto() {
        var dto = new UserProviderDto();
        dto.setId(1L);
        dto.setFirstName("Ivan");
        dto.setLastName("Petrov");
        dto.setEmail("ivan@example.com");
        dto.setDateCreated(OffsetDateTime.parse("2026-01-02T03:04:05Z"));
        dto.setClients(List.of(new UserProviderDto.Client(9L), new UserProviderDto.Client(10L)));
        return dto;
    }

    @Test
    void map_fullDto_mapsAllFields() {
        User result = mapper.map(fullDto());

        assertEquals(1L, result.getId());
        assertEquals("Ivan Petrov", result.getFullName());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Petrov", result.getLastName());
        assertEquals("ivan@example.com", result.getEmail());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDateCreated());
    }

    @Test
    void combineFullName_lastNameNull_usesFirstName() {
        var dto = fullDto();
        dto.setLastName(null);

        User result = mapper.map(dto);

        assertEquals("Ivan", result.getFullName());
    }

    @Test
    void combineFullName_bothNull_mapsNullFullName() {
        var dto = fullDto();
        dto.setFirstName(null);
        dto.setLastName(null);

        User result = mapper.map(dto);

        assertNull(result.getFullName());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
