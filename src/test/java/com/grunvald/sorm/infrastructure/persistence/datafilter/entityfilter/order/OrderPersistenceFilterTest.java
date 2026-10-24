package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order;

import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DomainNameBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.IpAddressBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.PersonBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.DateTimeCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermFullTextCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientPersistenceFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.InvoicePersistenceFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayDeque;
import java.util.List;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.QInvoice.INVOICE;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order.QOrder.ORDER;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order.QOrderItem.ORDER_ITEM;
import static com.querydsl.core.JoinType.INNERJOIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderPersistenceFilterTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    @Test
    void allNulls_returnsNull() {
        var filter = new OrderPersistenceFilter();
        var joins = new ArrayDeque<Join>();

        assertNull(filter.toPredicate(joins));
        assertTrue(joins.isEmpty());
        assertEquals(ORDER, filter.getQEntity());
    }

    @Test
    void client_joinsOrderToClient() {
        var filter = new OrderPersistenceFilter();
        filter.setClient(clientByName("john"));

        var joins = new ArrayDeque<Join>();
        var sql = filter.toPredicate(joins).toString();

        assertTrue(sql.contains("to_tsquery('simple', john)"));
        assertEquals(1, joins.size());
        var join = joins.pollFirst();
        assertEquals(INNERJOIN, join.getType());
        assertEquals(ORDER.getClientId(), join.getSourceId());
        assertEquals(CLIENT, join.getTargetEntity());
        assertEquals(CLIENT.getId(), join.getTargetId());
    }

    @Test
    void invoice_joinsOrderToInvoice() {
        var filter = new OrderPersistenceFilter();
        var invoice = new InvoicePersistenceFilter();
        invoice.setUpdatedAt(dateFilter(DATE));
        filter.setInvoice(invoice);

        var joins = new ArrayDeque<Join>();
        var sql = filter.toPredicate(joins).toString();

        assertTrue(sql.contains("= 2026-01-02T03:04:05Z"));
        assertEquals(1, joins.size());
        var join = joins.pollFirst();
        assertEquals(INNERJOIN, join.getType());
        assertEquals(ORDER.getInvoiceId(), join.getSourceId());
        assertEquals(INVOICE, join.getTargetEntity());
    }

    @Test
    void items_joinsOrderToOrderItem() {
        var filter = new OrderPersistenceFilter();
        var items = new OrderItemPersistenceFilter();
        items.setDomain(new DomainNameBaseFilter(
                new StringRegExpExactCondition(null, "example.com", null), null, null, null
        ));
        filter.setItems(items);

        var joins = new ArrayDeque<Join>();
        var sql = filter.toPredicate(joins).toString();

        assertTrue(sql.contains("= example.com"));
        assertEquals(1, joins.size());
        var join = joins.pollFirst();
        assertEquals(INNERJOIN, join.getType());
        assertEquals(ORDER.getId(), join.getSourceId());
        assertEquals(ORDER_ITEM.getOrderId(), join.getTargetId());
        assertEquals(ORDER_ITEM, join.getTargetEntity());
    }

    @Test
    void dateAndIpAddress_applyConditions() {
        var filter = new OrderPersistenceFilter();
        filter.setDate(dateFilter(DATE));
        filter.setIpAddress(new IpAddressBaseFilter(null, IpType.IPV4, null, null, null));

        var sql = filter.toPredicate(new ArrayDeque<Join>()).toString();

        assertTrue(sql.contains("= 2026-01-02T03:04:05Z"));
        assertTrue(sql.contains("like %:%"));
    }

    @Test
    void and_or_not_combineNestedFilters() {
        var filter = new OrderPersistenceFilter();
        filter.setAnd(List.of(orderByDate(DATE), orderByDate(DATE.plusDays(1))));
        filter.setOr(List.of(orderByDate(DATE.plusDays(2)), orderByDate(DATE.plusDays(3))));
        filter.setNot(orderByDate(DATE.plusDays(4)));

        var sql = filter.toPredicate(new ArrayDeque<Join>()).toString();

        assertTrue(sql.contains("= 2026-01-02T03:04:05Z && ord.date = 2026-01-03T03:04:05Z"));
        assertTrue(sql.contains("= 2026-01-04T03:04:05Z || ord.date = 2026-01-05T03:04:05Z"));
        assertTrue(sql.contains("!(ord.date = 2026-01-06T03:04:05Z)"));
    }

    private static OrderPersistenceFilter orderByDate(OffsetDateTime date) {
        var filter = new OrderPersistenceFilter();
        filter.setDate(dateFilter(date));
        return filter;
    }

    private static ClientPersistenceFilter clientByName(String term) {
        var client = new ClientPersistenceFilter();
        client.setName(new PersonBaseFilter(
                new StringTermFullTextCondition(List.of(term), null, null, null), null, null, null, null, null
        ));
        return client;
    }

    private static DateTimeBaseFilter dateFilter(OffsetDateTime date) {
        return new DateTimeBaseFilter(
                new DateTimeCondition(date, null, null, null, null, null, null), null, null, null
        );
    }
}
