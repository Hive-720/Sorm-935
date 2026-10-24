package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.contact;

import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.*;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.*;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientPersistenceFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayDeque;
import java.util.List;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.contact.QContact.CONTACT;
import static com.querydsl.core.JoinType.INNERJOIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContactFilterTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    @Test
    void allNulls_returnsNull() {
        var filter = new ContactFilter();
        var joins = new ArrayDeque<Join>();

        assertNull(filter.toPredicate(joins));
        assertTrue(joins.isEmpty());
        assertEquals(CONTACT, filter.getQEntity());
    }

    @Test
    void allFields_addConditionsAndClientJoin() {
        var filter = new ContactFilter();
        filter.setClient(clientByName("john"));
        filter.setName(new PersonBaseFilter(
                new StringTermFullTextCondition(List.of("peter"), null, null, null), null, null, null, null, null
        ));
        filter.setOrganizationInfo(new OrganizationInfoBaseFilter(
                new StringRegExpExactCondition(null, "ООО Ромашка", null), null, null, null
        ));
        filter.setEmail(new EmailBaseFilter(new StringRegExpExactCondition(null, "a@b.com", null), null, null, null));
        filter.setAddress(new AddressBaseFilter(
                null, new StringExactCondition("10001", null), null, null, null, null, null, null
        ));
        filter.setInn(new InnBaseFilter(new StringTermExactCondition(null, null, "7711223344", null), null, null, null));
        filter.setMsisdn(new MsisdnBaseFilter(new StringRegExpExactCondition(null, "79991234567", null), null, null, null));
        filter.setUpdatedAt(dateFilter(DATE));
        filter.setCreatedAt(dateFilter(DATE.plusDays(1)));

        var joins = new ArrayDeque<Join>();
        var sql = filter.toPredicate(joins).toString();

        assertTrue(sql.contains("to_tsquery('simple', john)"));
        assertTrue(sql.contains("to_tsquery('simple', peter)"));
        assertTrue(sql.contains("= ООО Ромашка"));
        assertTrue(sql.contains("= a@b.com"));
        assertTrue(sql.contains("= 10001"));
        assertTrue(sql.contains("= 7711223344"));
        assertTrue(sql.contains("= 79991234567"));
        assertTrue(sql.contains("= 2026-01-02T03:04:05Z"));
        assertTrue(sql.contains("= 2026-01-03T03:04:05Z"));

        assertEquals(1, joins.size());
        var join = joins.pollFirst();
        assertEquals(INNERJOIN, join.getType());
        assertEquals(CONTACT.getClientId(), join.getSourceId());
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
