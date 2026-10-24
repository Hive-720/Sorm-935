package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.transaction.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport;
import com.grunvald.sorm.service.whmcsdata.filter.TransactionFilter;
import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D01;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D08;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D09;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransactionFilterPersistenceMapperTest {

    private final TransactionFilterPersistenceMapper mapper = new TransactionFilterPersistenceMapperImpl();

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }

    @Test
    void map_allFieldsAreMapped() {
        var result = mapper.map(PersistenceFilterTestSupport.transaction());

        assertNotNull(result.getInvoice());
        PersistenceFilterTestSupport.assertDateTime(result.getInvoice().getDate(), D01);
        PersistenceFilterTestSupport.assertEmail(result.getInvoice().getClient().getEmail(), "client@mail.com");

        assertNotNull(result.getClient());
        PersistenceFilterTestSupport.assertEmail(result.getClient().getEmail(), "client@mail.com");
        PersistenceFilterTestSupport.assertMsisdn(result.getClient().getMsisdn());

        PersistenceFilterTestSupport.assertDateTime(result.getDate(), D08);

        assertEquals(1, result.getAnd().size());
        PersistenceFilterTestSupport.assertDateTime(result.getAnd().get(0).getDate(), D09);
        assertEquals(1, result.getOr().size());
        PersistenceFilterTestSupport.assertDateTime(result.getOr().get(0).getDate(), D09);
        assertNotNull(result.getNot());
        PersistenceFilterTestSupport.assertDateTime(result.getNot().getDate(), D09);
    }

    @Test
    void map_emptyFilter_mapsToEmptyFilter() {
        var result = mapper.map(new TransactionFilter());

        assertNotNull(result);
        assertNull(result.getInvoice());
        assertNull(result.getClient());
        assertNull(result.getDate());
        assertNull(result.getAnd());
        assertNull(result.getOr());
        assertNull(result.getNot());
    }
}
