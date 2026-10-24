package com.grunvald.sorm.infrastructure.persistence.data.user.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.user.entity.relation.UserClientRelationDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.userClientRelationPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserClientRelationPersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private UserClientRelationPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = userClientRelationPersistenceMapper();
    }

    @Test
    void shouldMapDbToModel() {
        UserClientRelationDb db = new UserClientRelationDb();
        db.setClientId(1L);
        db.setUserId(2L);
        db.setCreatedAt(DATE);

        var result = mapper.map(db);

        assertEquals(1L, result.getClientId());
        assertEquals(2L, result.getUserId());
        assertEquals(DATE, result.getCreatedAt());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.user.model.UserClientRelation(2L, 1L, DATE);

        UserClientRelationDb result = mapper.map(model);

        assertEquals(1L, result.getClientId());
        assertEquals(2L, result.getUserId());
        assertEquals(DATE, result.getCreatedAt());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((UserClientRelationDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.user.model.UserClientRelation) null));
    }
}
