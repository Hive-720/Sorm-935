package com.grunvald.sorm.infrastructure.persistence.data.activitylog.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.activitylog.entity.ActivityLogDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.activityLogPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ActivityLogPersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ActivityLogPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = activityLogPersistenceMapper();
    }

    @Test
    void shouldMapDbToModel() {
        ActivityLogDb db = new ActivityLogDb();
        db.setId(1L);
        db.setClientId(2L);
        db.setUserId(3L);
        db.setDescription("login");
        db.setUsername("admin");
        db.setIpAddress("10.0.0.1");
        db.setDate(DATE);

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(3L, result.getUserId());
        assertEquals("login", result.getDescription());
        assertEquals("admin", result.getUsername());
        assertEquals("10.0.0.1", result.getIpAddress());
        assertEquals(DATE, result.getDate());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog();
        model.setId(1L);
        model.setClientId(2L);
        model.setUserId(3L);
        model.setDescription("login");
        model.setUsername("admin");
        model.setIpAddress("10.0.0.1");
        model.setDate(DATE);

        ActivityLogDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(3L, result.getUserId());
        assertEquals("login", result.getDescription());
        assertEquals("admin", result.getUsername());
        assertEquals("10.0.0.1", result.getIpAddress());
        assertEquals(DATE, result.getDate());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((ActivityLogDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog) null));
    }
}
