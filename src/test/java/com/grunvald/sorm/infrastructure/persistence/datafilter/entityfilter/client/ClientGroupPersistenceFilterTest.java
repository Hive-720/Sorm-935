package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client;

import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.DateTimeCondition;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayDeque;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClientGroup.CLIENT_GROUP;
import static org.junit.jupiter.api.Assertions.*;

class ClientGroupPersistenceFilterTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    @Test
    void allNulls_returnsNull() {
        var filter = new ClientGroupPersistenceFilter();
        var joins = new ArrayDeque<Join>();

        assertNull(filter.toPredicate(joins));
        assertTrue(joins.isEmpty());
        assertEquals(CLIENT_GROUP, filter.getQEntity());
    }

    @Test
    void updatedAt_appliesCondition() {
        var filter = new ClientGroupPersistenceFilter();
        filter.setUpdatedAt(new DateTimeBaseFilter(
                new DateTimeCondition(DATE, null, null, null, null, null, null), null, null, null
        ));

        var predicate = filter.toPredicate(new ArrayDeque<Join>());

        assertNotNull(predicate);
        assertTrue(predicate.toString().contains("= 2026-01-02T03:04:05Z"));
    }
}
