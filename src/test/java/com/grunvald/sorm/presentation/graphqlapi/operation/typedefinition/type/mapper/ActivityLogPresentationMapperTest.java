package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ActivityLog;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpAddressBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.LoginBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.activityLogPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ActivityLogPresentationMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ActivityLogPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = activityLogPresentationMapper();
    }

    @Test
    void shouldMapAllFields() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog();
        source.setId(42L);
        source.setClientId(100L);
        source.setUserId(200L);
        source.setDescription("login");
        source.setUsername("admin");
        source.setIpAddress("192.168.1.1");
        source.setDate(DATE);

        ActivityLog result = mapper.map(source);

        assertEquals("42", result.id());
        assertEquals(100L, result.clientId());
        assertEquals(200L, result.userId());
        assertEquals("login", result.description());
        assertEquals(new LoginBase("admin"), result.username());
        assertEquals(new IpAddressBase("192.168.1.1", IpType.IPV4), result.ipAddress());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.date());
    }

    @Test
    void shouldMapIpv6Address() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog();
        source.setIpAddress("2001:db8::1");

        ActivityLog result = mapper.map(source);

        assertEquals(new IpAddressBase("2001:db8::1", IpType.IPV6), result.ipAddress());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
