package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.activitylog.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport;
import com.grunvald.sorm.service.whmcsdata.filter.ActivityLogFilter;
import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D05;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D06;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ActivityLogFilterPersistenceMapperTest {

    private final ActivityLogFilterPersistenceMapper mapper = new ActivityLogFilterPersistenceMapperImpl();

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }

    @Test
    void map_allFieldsAreMapped() {
        var result = mapper.map(PersistenceFilterTestSupport.activityLog());

        assertNotNull(result.getClient());
        PersistenceFilterTestSupport.assertEmail(result.getClient().getEmail(), "client@mail.com");
        assertNotNull(result.getUser());
        PersistenceFilterTestSupport.assertPerson(result.getUser().getName());
        PersistenceFilterTestSupport.assertEmail(result.getUser().getEmail(), "user@mail.com");
        PersistenceFilterTestSupport.assertLogin(result.getUsername());
        PersistenceFilterTestSupport.assertIpAddress(result.getIpAddress());
        PersistenceFilterTestSupport.assertDateTime(result.getDate(), D05);

        assertEquals(1, result.getAnd().size());
        PersistenceFilterTestSupport.assertDateTime(result.getAnd().get(0).getDate(), D06);
        assertEquals(1, result.getOr().size());
        PersistenceFilterTestSupport.assertDateTime(result.getOr().get(0).getDate(), D06);
        assertNotNull(result.getNot());
        PersistenceFilterTestSupport.assertDateTime(result.getNot().getDate(), D06);
    }

    @Test
    void map_emptyFilter_mapsToEmptyFilter() {
        var result = mapper.map(new ActivityLogFilter());

        assertNotNull(result);
        assertNull(result.getClient());
        assertNull(result.getUser());
        assertNull(result.getUsername());
        assertNull(result.getIpAddress());
        assertNull(result.getDate());
        assertNull(result.getAnd());
        assertNull(result.getOr());
        assertNull(result.getNot());
    }
}
