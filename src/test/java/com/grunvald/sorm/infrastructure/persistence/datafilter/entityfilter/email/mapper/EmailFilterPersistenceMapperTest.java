package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.email.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport;
import com.grunvald.sorm.service.whmcsdata.filter.EmailFilter;
import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D01;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D02;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D03;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailFilterPersistenceMapperTest {

    private final EmailFilterPersistenceMapper mapper = new EmailFilterPersistenceMapperImpl();

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }

    @Test
    void map_allFieldsAreMapped() {
        var result = mapper.map(PersistenceFilterTestSupport.email());

        assertNotNull(result.getClient());
        PersistenceFilterTestSupport.assertEmail(result.getClient().getEmail(), "client@mail.com");
        PersistenceFilterTestSupport.assertEmail(result.getTo(), "to@mail.com");
        PersistenceFilterTestSupport.assertEmail(result.getCc(), "cc@mail.com");
        PersistenceFilterTestSupport.assertEmail(result.getBcc(), "bcc@mail.com");
        PersistenceFilterTestSupport.assertDateTime(result.getDate(), D01);
        PersistenceFilterTestSupport.assertDateTime(result.getCreatedAt(), D02);
        PersistenceFilterTestSupport.assertDateTime(result.getUpdatedAt(), D03);

        assertEquals(1, result.getAnd().size());
        PersistenceFilterTestSupport.assertEmail(result.getAnd().getFirst().getTo(), "child-to@mail.com");
        assertEquals(1, result.getOr().size());
        PersistenceFilterTestSupport.assertEmail(result.getOr().getFirst().getTo(), "child-to@mail.com");
        assertNotNull(result.getNot());
        PersistenceFilterTestSupport.assertEmail(result.getNot().getTo(), "child-to@mail.com");
    }

    @Test
    void map_emptyFilter_mapsToEmptyFilter() {
        var result = mapper.map(new EmailFilter());

        assertNotNull(result);
        assertNull(result.getClient());
        assertNull(result.getTo());
        assertNull(result.getCc());
        assertNull(result.getBcc());
        assertNull(result.getDate());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
        assertNull(result.getAnd());
        assertNull(result.getOr());
        assertNull(result.getNot());
    }
}
