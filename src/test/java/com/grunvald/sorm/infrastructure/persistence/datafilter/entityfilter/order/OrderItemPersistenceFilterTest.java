package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order;

import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DomainNameBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order.QOrderItem.ORDER_ITEM;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemPersistenceFilterTest {

    @Test
    void allNulls_returnsNull() {
        var filter = new OrderItemPersistenceFilter();
        var joins = new ArrayDeque<Join>();

        assertNull(filter.toPredicate(joins));
        assertTrue(joins.isEmpty());
        assertEquals(ORDER_ITEM, filter.getQEntity());
    }

    @Test
    void domain_appliesCondition() {
        var filter = new OrderItemPersistenceFilter();
        filter.setDomain(new DomainNameBaseFilter(
                new StringRegExpExactCondition(null, "example.com", null), null, null, null
        ));

        var predicate = filter.toPredicate(new ArrayDeque<Join>());

        assertNotNull(predicate);
        assertTrue(predicate.toString().contains("= example.com"));
    }
}
