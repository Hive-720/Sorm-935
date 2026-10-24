package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog;
import com.grunvald.sorm.service.whmcsprovider.dto.ActivityLogProviderDto;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ActivityLogMapperTest {

    private final ActivityLogMapper mapper = new ActivityLogMapperImpl();

    @Test
    void map_fullDto_mapsAllFields() {
        var dto = new ActivityLogProviderDto();
        dto.setId(1L);
        dto.setClientId(2L);
        dto.setUserId(3L);
        dto.setDescription("description");
        dto.setUsername("username");
        dto.setIpAddress("1.2.3.4");
        dto.setDate(OffsetDateTime.parse("2026-01-02T03:04:05Z"));

        ActivityLog result = mapper.map(dto);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(3L, result.getUserId());
        assertEquals("description", result.getDescription());
        assertEquals("username", result.getUsername());
        assertEquals("1.2.3.4", result.getIpAddress());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDate());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
