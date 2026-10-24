package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.credit;

import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.PersonBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.DateTimeCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermFullTextCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientPersistenceFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayDeque;
import java.util.List;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.credit.QCredit.CREDIT;
import static com.querydsl.core.JoinType.INNERJOIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreditFilterTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    @Test
    void allNulls_returnsNull() {
        var filter = new CreditFilter();
        var joins = new ArrayDeque<Join>();

        assertNull(filter.toPredicate(joins));
        assertTrue(joins.isEmpty());
        assertEquals(CREDIT, filter.getQEntity());
    }

    @Test
    void allFields_addConditionsAndClientJoin() {
        var filter = new CreditFilter();
        filter.setClient(clientByName("john"));
        filter.setDate(dateFilter(DATE));

        var joins = new ArrayDeque<Join>();
        var sql = filter.toPredicate(joins).toString();

        assertTrue(sql.contains("to_tsquery('simple', john)"));
        assertTrue(sql.contains("= 2026-01-02T03:04:05Z"));

        assertEquals(1, joins.size());
        var join = joins.pollFirst();
        assertEquals(INNERJOIN, join.getType());
        assertEquals(CREDIT.getClientId(), join.getSourceId());
        assertEquals(CLIENT, join.getTargetEntity());
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
