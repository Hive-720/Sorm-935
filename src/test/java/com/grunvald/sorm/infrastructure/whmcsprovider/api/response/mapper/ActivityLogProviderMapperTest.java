package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetActivityLogResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.ActivityLogProviderDto;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ActivityLogProviderMapperTest {

    private final ActivityLogProviderMapper mapper = new ActivityLogProviderMapperImpl();

    @Test
    void map_fullActivityLog_mapsAllFields() {
        var source = new GetActivityLogResponse.Activity.ActivityLog();
        source.setId(1L);
        source.setClientId(2L);
        source.setUserId(3L);
        source.setAdminId(5L);
        source.setDate(OffsetDateTime.parse("2026-01-02T03:04:05Z"));
        source.setDescription("description");
        source.setUsername("username");
        source.setIpAddress("1.2.3.4");

        ActivityLogProviderDto result = mapper.map(source);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(3L, result.getUserId());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDate());
        assertEquals("description", result.getDescription());
        assertEquals("username", result.getUsername());
        assertEquals("1.2.3.4", result.getIpAddress());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
