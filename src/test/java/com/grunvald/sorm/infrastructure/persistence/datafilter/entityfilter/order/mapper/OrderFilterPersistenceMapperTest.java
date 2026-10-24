package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport;
import com.grunvald.sorm.service.whmcsdata.filter.OrderFilter;
import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D01;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.PersistenceFilterTestSupport.D09;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderFilterPersistenceMapperTest {

    private final OrderFilterPersistenceMapper mapper = new OrderFilterPersistenceMapperImpl();

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }

    @Test
    void map_allFieldsAreMapped() {
        var result = mapper.map(PersistenceFilterTestSupport.order());

        assertNotNull(result.getClient());
        PersistenceFilterTestSupport.assertEmail(result.getClient().getEmail(), "client@mail.com");

        assertNotNull(result.getInvoice());
        PersistenceFilterTestSupport.assertDateTime(result.getInvoice().getDate(), D01);
        PersistenceFilterTestSupport.assertEmail(result.getInvoice().getClient().getEmail(), "client@mail.com");

        assertNotNull(result.getItems());
        PersistenceFilterTestSupport.assertDomainName(result.getItems().getDomain(), "a.ru");
        assertEquals(1, result.getItems().getAnd().size());
        PersistenceFilterTestSupport.assertDomainName(result.getItems().getAnd().get(0).getDomain(), "child.ru");
        assertEquals(1, result.getItems().getOr().size());
        PersistenceFilterTestSupport.assertDomainName(result.getItems().getOr().get(0).getDomain(), "child.ru");
        assertNotNull(result.getItems().getNot());
        PersistenceFilterTestSupport.assertDomainName(result.getItems().getNot().getDomain(), "child.ru");

        PersistenceFilterTestSupport.assertDateTime(result.getDate(), D01);
        PersistenceFilterTestSupport.assertIpAddress(result.getIpAddress());

        assertEquals(1, result.getAnd().size());
        PersistenceFilterTestSupport.assertDateTime(result.getAnd().get(0).getDate(), D09);
        assertEquals(1, result.getOr().size());
        PersistenceFilterTestSupport.assertDateTime(result.getOr().get(0).getDate(), D09);
        assertNotNull(result.getNot());
        PersistenceFilterTestSupport.assertDateTime(result.getNot().getDate(), D09);
    }

    @Test
    void map_emptyFilter_mapsToEmptyFilter() {
        var result = mapper.map(new OrderFilter());

        assertNotNull(result);
        assertNull(result.getClient());
        assertNull(result.getInvoice());
        assertNull(result.getItems());
        assertNull(result.getIpAddress());
        assertNull(result.getDate());
        assertNull(result.getAnd());
        assertNull(result.getOr());
        assertNull(result.getNot());
    }
}
