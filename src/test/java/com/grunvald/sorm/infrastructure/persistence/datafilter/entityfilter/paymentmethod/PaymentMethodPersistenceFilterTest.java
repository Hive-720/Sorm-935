package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.paymentmethod;

import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.DateTimeCondition;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayDeque;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.paymentmethod.QPaymentMethod.PAYMENT_METHOD;
import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodPersistenceFilterTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    @Test
    void allNulls_returnsNull() {
        var filter = new PaymentMethodPersistenceFilter();
        var joins = new ArrayDeque<Join>();

        assertNull(filter.toPredicate(joins));
        assertTrue(joins.isEmpty());
        assertEquals(PAYMENT_METHOD, filter.getQEntity());
    }

    @Test
    void updatedAt_appliesCondition() {
        var filter = new PaymentMethodPersistenceFilter();
        filter.setUpdatedAt(new DateTimeBaseFilter(
                new DateTimeCondition(DATE, null, null, null, null, null, null), null, null, null
        ));

        var predicate = filter.toPredicate(new ArrayDeque<Join>());

        assertNotNull(predicate);
        assertTrue(predicate.toString().contains("= 2026-01-02T03:04:05Z"));
    }
}
