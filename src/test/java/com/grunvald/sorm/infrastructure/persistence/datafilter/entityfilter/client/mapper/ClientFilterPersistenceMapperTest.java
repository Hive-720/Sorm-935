package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport;
import com.grunvald.sorm.service.whmcsdata.filter.ClientFilter;
import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D01;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientFilterPersistenceMapperTest {

    private final ClientFilterPersistenceMapper mapper = new ClientFilterPersistenceMapperImpl();

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }

    @Test
    void map_allFieldsAreMapped() {
        var result = mapper.map(PersistenceFilterTestSupport.client());

        PersistenceFilterTestSupport.assertPerson(result.getName());
        assertNotNull(result.getUserOwner());
        PersistenceFilterTestSupport.assertPerson(result.getUserOwner().getName());
        PersistenceFilterTestSupport.assertEmail(result.getUserOwner().getEmail(), "user@mail.com");
        assertNotNull(result.getClientGroup());
        PersistenceFilterTestSupport.assertDateTime(result.getClientGroup().getUpdatedAt(), D01);
        PersistenceFilterTestSupport.assertOrganizationInfo(result.getOrganizationInfo());
        PersistenceFilterTestSupport.assertInn(result.getInn());
        PersistenceFilterTestSupport.assertEmail(result.getEmail(), "client@mail.com");
        PersistenceFilterTestSupport.assertMsisdn(result.getMsisdn());
        PersistenceFilterTestSupport.assertMcc(result.getMcc());
        PersistenceFilterTestSupport.assertAddress(result.getAddress());

        assertEquals(1, result.getAnd().size());
        PersistenceFilterTestSupport.assertEmail(result.getAnd().get(0).getEmail(), "nested-client@mail.com");
        assertEquals(1, result.getOr().size());
        PersistenceFilterTestSupport.assertEmail(result.getOr().get(0).getEmail(), "nested-client@mail.com");
        assertNotNull(result.getNot());
        PersistenceFilterTestSupport.assertEmail(result.getNot().getEmail(), "nested-client@mail.com");
    }

    @Test
    void map_emptyFilter_mapsToEmptyFilter() {
        var result = mapper.map(new ClientFilter());

        assertNotNull(result);
        assertNull(result.getName());
        assertNull(result.getUserOwner());
        assertNull(result.getClientGroup());
        assertNull(result.getOrganizationInfo());
        assertNull(result.getInn());
        assertNull(result.getEmail());
        assertNull(result.getMsisdn());
        assertNull(result.getMcc());
        assertNull(result.getAddress());
        assertNull(result.getAnd());
        assertNull(result.getOr());
        assertNull(result.getNot());
    }
}
