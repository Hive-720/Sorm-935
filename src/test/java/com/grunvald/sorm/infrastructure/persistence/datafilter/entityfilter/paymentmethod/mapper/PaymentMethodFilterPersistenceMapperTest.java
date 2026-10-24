package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.paymentmethod.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport;
import com.grunvald.sorm.service.whmcsdata.filter.PaymentMethodFilter;
import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D01;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D02;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentMethodFilterPersistenceMapperTest {

    private final PaymentMethodFilterPersistenceMapper mapper = new PaymentMethodFilterPersistenceMapperImpl();

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }

    @Test
    void map_allFieldsAreMapped() {
        var result = mapper.map(PersistenceFilterTestSupport.paymentMethod());

        PersistenceFilterTestSupport.assertDateTime(result.getUpdatedAt(), D01);
        assertEquals(1, result.getAnd().size());
        PersistenceFilterTestSupport.assertDateTime(result.getAnd().get(0).getUpdatedAt(), D02);
        assertEquals(1, result.getOr().size());
        PersistenceFilterTestSupport.assertDateTime(result.getOr().get(0).getUpdatedAt(), D02);
        assertNotNull(result.getNot());
        PersistenceFilterTestSupport.assertDateTime(result.getNot().getUpdatedAt(), D02);
    }

    @Test
    void map_emptyFilter_mapsToEmptyFilter() {
        var result = mapper.map(new PaymentMethodFilter());

        assertNotNull(result);
        assertNull(result.getUpdatedAt());
        assertNull(result.getAnd());
        assertNull(result.getOr());
        assertNull(result.getNot());
    }
}
