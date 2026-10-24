package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport;
import com.grunvald.sorm.service.whmcsdata.filter.InvoiceFilter;
import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D01;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D02;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D03;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D04;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D05;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D06;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D07;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D08;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class InvoiceFilterPersistenceMapperTest {

    private final InvoiceFilterPersistenceMapper mapper = new InvoiceFilterPersistenceMapperImpl();

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }

    @Test
    void map_allFieldsAreMapped() {
        var result = mapper.map(PersistenceFilterTestSupport.invoice());

        assertNotNull(result.getClient());
        PersistenceFilterTestSupport.assertEmail(result.getClient().getEmail(), "client@mail.com");
        PersistenceFilterTestSupport.assertDateTime(result.getDate(), D01);
        PersistenceFilterTestSupport.assertDateTime(result.getDueDate(), D02);
        PersistenceFilterTestSupport.assertDateTime(result.getDatePaid(), D03);
        PersistenceFilterTestSupport.assertDateTime(result.getDateRefunded(), D04);
        PersistenceFilterTestSupport.assertDateTime(result.getDateCancelled(), D05);
        PersistenceFilterTestSupport.assertDateTime(result.getCreatedAt(), D06);
        PersistenceFilterTestSupport.assertDateTime(result.getUpdatedAt(), D07);

        assertEquals(1, result.getAnd().size());
        PersistenceFilterTestSupport.assertDateTime(result.getAnd().get(0).getDate(), D08);
        assertEquals(1, result.getOr().size());
        PersistenceFilterTestSupport.assertDateTime(result.getOr().get(0).getDate(), D08);
        assertNotNull(result.getNot());
        PersistenceFilterTestSupport.assertDateTime(result.getNot().getDate(), D08);
    }

    @Test
    void map_emptyFilter_mapsToEmptyFilter() {
        var result = mapper.map(new InvoiceFilter());

        assertNotNull(result);
        assertNull(result.getClient());
        assertNull(result.getDate());
        assertNull(result.getDueDate());
        assertNull(result.getDatePaid());
        assertNull(result.getDateRefunded());
        assertNull(result.getDateCancelled());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
        assertNull(result.getAnd());
        assertNull(result.getOr());
        assertNull(result.getNot());
    }
}
