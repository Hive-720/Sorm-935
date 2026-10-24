package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.user.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport;
import com.grunvald.sorm.service.whmcsdata.filter.UserFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserFilterPersistenceMapperTest {

    private final UserFilterPersistenceMapper mapper = new UserFilterPersistenceMapperImpl();

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }

    @Test
    void map_allFieldsAreMapped() {
        var result = mapper.map(PersistenceFilterTestSupport.user());

        PersistenceFilterTestSupport.assertPerson(result.getName());
        PersistenceFilterTestSupport.assertEmail(result.getEmail(), "user@mail.com");

        assertEquals(1, result.getAnd().size());
        assertNull(result.getAnd().getFirst().getName());
        PersistenceFilterTestSupport.assertEmail(result.getAnd().getFirst().getEmail(), "nested-user@mail.com");
        assertEquals(1, result.getOr().size());
        assertNull(result.getOr().getFirst().getName());
        PersistenceFilterTestSupport.assertEmail(result.getOr().getFirst().getEmail(), "nested-user@mail.com");
        assertNotNull(result.getNot());
        assertNull(result.getNot().getName());
        PersistenceFilterTestSupport.assertEmail(result.getNot().getEmail(), "nested-user@mail.com");
    }

    @Test
    void map_emptyFilter_mapsToEmptyFilter() {
        var result = mapper.map(new UserFilter());

        assertNotNull(result);
        assertNull(result.getName());
        assertNull(result.getEmail());
        assertNull(result.getAnd());
        assertNull(result.getOr());
        assertNull(result.getNot());
    }
}
