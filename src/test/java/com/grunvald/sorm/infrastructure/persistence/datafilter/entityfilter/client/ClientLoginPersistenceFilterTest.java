package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client;

import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.IpAddressBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.PersonBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.DateTimeCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermFullTextCondition;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayDeque;
import java.util.List;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClientLogin.CLIENT_LOGIN;
import static com.querydsl.core.JoinType.INNERJOIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientLoginPersistenceFilterTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    @Test
    void allNulls_returnsNull() {
        var filter = new ClientLoginPersistenceFilter();
        var joins = new ArrayDeque<Join>();

        assertNull(filter.toPredicate(joins));
        assertTrue(joins.isEmpty());
        assertEquals(CLIENT_LOGIN, filter.getQEntity());
    }

    @Test
    void allFields_addConditionsAndClientJoin() {
        var filter = new ClientLoginPersistenceFilter();
        var client = new ClientPersistenceFilter();
        client.setName(new PersonBaseFilter(
                new StringTermFullTextCondition(List.of("john"), null, null, null), null, null, null, null, null
        ));
        filter.setClient(client);
        filter.setIpAddress(new IpAddressBaseFilter(null, IpType.IPV4, null, null, null));
        filter.setDate(new DateTimeBaseFilter(
                new DateTimeCondition(DATE, null, null, null, null, null, null), null, null, null
        ));

        var joins = new ArrayDeque<Join>();
        var sql = filter.toPredicate(joins).toString();

        assertTrue(sql.contains("to_tsquery('simple', john)"));
        assertTrue(sql.contains("like %:%"));
        assertTrue(sql.contains("= 2026-01-02T03:04:05Z"));

        assertEquals(1, joins.size());
        var join = joins.pollFirst();
        assertEquals(INNERJOIN, join.getType());
        assertEquals(CLIENT_LOGIN.getClientId(), join.getSourceId());
        assertEquals(CLIENT, join.getTargetEntity());
    }
}
