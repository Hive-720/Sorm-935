package com.grunvald.sorm.infrastructure.persistence.data.user.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.user.entity.UserDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.userPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserPersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private UserPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = userPersistenceMapper();
    }

    @Test
    void shouldMapDbToModel() {
        UserDb db = new UserDb();
        db.setId(1L);
        db.setFullName("Sidorov Sidor");
        db.setFirstName("Sidor");
        db.setLastName("Sidorov");
        db.setEmail("sidor@example.com");
        db.setDateCreated(DATE);

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals("Sidorov Sidor", result.getFullName());
        assertEquals("Sidor", result.getFirstName());
        assertEquals("Sidorov", result.getLastName());
        assertEquals("sidor@example.com", result.getEmail());
        assertEquals(DATE, result.getDateCreated());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.user.model.User();
        model.setId(1L);
        model.setFullName("Sidorov Sidor");
        model.setFirstName("Sidor");
        model.setLastName("Sidorov");
        model.setEmail("sidor@example.com");
        model.setDateCreated(DATE);

        UserDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals("Sidorov Sidor", result.getFullName());
        assertEquals("Sidor", result.getFirstName());
        assertEquals("Sidorov", result.getLastName());
        assertEquals("sidor@example.com", result.getEmail());
        assertEquals(DATE, result.getDateCreated());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((UserDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.user.model.User) null));
    }
}
