package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.affiliate.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport;
import com.grunvald.sorm.service.whmcsdata.filter.AffiliateFilter;
import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D01;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D02;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D03;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D04;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AffiliateFilterPersistenceMapperTest {

    private final AffiliateFilterPersistenceMapper mapper = new AffiliateFilterPersistenceMapperImpl();

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }

    @Test
    void map_allFieldsAreMapped() {
        var result = mapper.map(PersistenceFilterTestSupport.affiliate());

        assertNotNull(result.getClient());
        PersistenceFilterTestSupport.assertEmail(result.getClient().getEmail(), "client@mail.com");
        PersistenceFilterTestSupport.assertInn(result.getClient().getInn());
        PersistenceFilterTestSupport.assertDateTime(result.getDate(), D01);
        PersistenceFilterTestSupport.assertDateTime(result.getCreatedAt(), D02);
        PersistenceFilterTestSupport.assertDateTime(result.getUpdatedAt(), D03);

        assertEquals(1, result.getAnd().size());
        PersistenceFilterTestSupport.assertDateTime(result.getAnd().get(0).getDate(), D04);
        assertEquals(1, result.getOr().size());
        PersistenceFilterTestSupport.assertDateTime(result.getOr().get(0).getDate(), D04);
        assertNotNull(result.getNot());
        PersistenceFilterTestSupport.assertDateTime(result.getNot().getDate(), D04);
    }

    @Test
    void map_emptyFilter_mapsToEmptyFilter() {
        var result = mapper.map(new AffiliateFilter());

        assertNotNull(result);
        assertNull(result.getClient());
        assertNull(result.getDate());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
        assertNull(result.getAnd());
        assertNull(result.getOr());
        assertNull(result.getNot());
    }
}
