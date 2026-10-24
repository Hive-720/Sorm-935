package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.credit.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport;
import com.grunvald.sorm.service.whmcsdata.filter.CreditFilter;
import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D01;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D02;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreditFilterPersistenceMapperTest {

    private final CreditFilterPersistenceMapper mapper = new CreditFilterPersistenceMapperImpl();

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }

    @Test
    void map_allFieldsAreMapped() {
        var result = mapper.map(PersistenceFilterTestSupport.credit());

        assertNotNull(result.getClient());
        PersistenceFilterTestSupport.assertMsisdn(result.getClient().getMsisdn());
        PersistenceFilterTestSupport.assertMcc(result.getClient().getMcc());
        PersistenceFilterTestSupport.assertDateTime(result.getDate(), D01);

        assertEquals(1, result.getAnd().size());
        PersistenceFilterTestSupport.assertDateTime(result.getAnd().getFirst().getDate(), D02);
        assertEquals(1, result.getOr().size());
        PersistenceFilterTestSupport.assertDateTime(result.getOr().getFirst().getDate(), D02);
        assertNotNull(result.getNot());
        PersistenceFilterTestSupport.assertDateTime(result.getNot().getDate(), D02);
    }

    @Test
    void map_emptyFilter_mapsToEmptyFilter() {
        var result = mapper.map(new CreditFilter());

        assertNotNull(result);
        assertNull(result.getClient());
        assertNull(result.getDate());
        assertNull(result.getAnd());
        assertNull(result.getOr());
        assertNull(result.getNot());
    }
}
