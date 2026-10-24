package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.contact.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport;
import com.grunvald.sorm.service.whmcsdata.filter.ContactFilter;
import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D07;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D08;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D09;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContactFilterPersistenceMapperTest {

    private final ContactFilterPersistenceMapper mapper = new ContactFilterPersistenceMapperImpl();

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }

    @Test
    void map_allFieldsAreMapped() {
        var result = mapper.map(PersistenceFilterTestSupport.contact());

        assertNotNull(result.getClient());
        PersistenceFilterTestSupport.assertEmail(result.getClient().getEmail(), "client@mail.com");
        PersistenceFilterTestSupport.assertPerson(result.getName());
        PersistenceFilterTestSupport.assertOrganizationInfo(result.getOrganizationInfo());
        PersistenceFilterTestSupport.assertEmail(result.getEmail(), "contact@mail.com");
        PersistenceFilterTestSupport.assertAddress(result.getAddress());
        PersistenceFilterTestSupport.assertInn(result.getInn());
        PersistenceFilterTestSupport.assertMsisdn(result.getMsisdn());
        PersistenceFilterTestSupport.assertDateTime(result.getUpdatedAt(), D07);
        PersistenceFilterTestSupport.assertDateTime(result.getCreatedAt(), D08);

        assertEquals(1, result.getAnd().size());
        PersistenceFilterTestSupport.assertDateTime(result.getAnd().get(0).getUpdatedAt(), D09);
        assertEquals(1, result.getOr().size());
        PersistenceFilterTestSupport.assertDateTime(result.getOr().get(0).getUpdatedAt(), D09);
        assertNotNull(result.getNot());
        PersistenceFilterTestSupport.assertDateTime(result.getNot().getUpdatedAt(), D09);
    }

    @Test
    void map_emptyFilter_mapsToEmptyFilter() {
        var result = mapper.map(new ContactFilter());

        assertNotNull(result);
        assertNull(result.getClient());
        assertNull(result.getName());
        assertNull(result.getOrganizationInfo());
        assertNull(result.getEmail());
        assertNull(result.getAddress());
        assertNull(result.getInn());
        assertNull(result.getMsisdn());
        assertNull(result.getUpdatedAt());
        assertNull(result.getCreatedAt());
        assertNull(result.getAnd());
        assertNull(result.getOr());
        assertNull(result.getNot());
    }
}
