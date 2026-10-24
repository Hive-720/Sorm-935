package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.activitylog;

import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.IpAddressBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.LoginFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.DateTimeCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermFullTextCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientPersistenceFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.user.UserPersistenceFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayDeque;
import java.util.List;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.activitylog.QActivityLog.ACTIVITY_LOG;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.user.QUser.USER;
import static com.querydsl.core.JoinType.INNERJOIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActivityLogPersistenceFilterTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    @Test
    void allNulls_returnsNull() {
        var filter = new ActivityLogPersistenceFilter();
        var joins = new ArrayDeque<Join>();

        assertNull(filter.toPredicate(joins));
        assertTrue(joins.isEmpty());
        assertEquals(ACTIVITY_LOG, filter.getQEntity());
    }

    @Test
    void allFields_addConditionsAndJoins() {
        var filter = new ActivityLogPersistenceFilter();
        filter.setClient(clientByName("john"));
        filter.setUser(userByEmail("u@x.com"));
        filter.setDate(dateFilter(DATE));
        filter.setUsername(new LoginFilter(new StringRegExpExactCondition(null, "admin", null), null, null, null));
        filter.setIpAddress(new IpAddressBaseFilter(null, IpType.IPV4, null, null, null));

        var joins = new ArrayDeque<Join>();
        var sql = filter.toPredicate(joins).toString();

        assertTrue(sql.contains("to_tsquery('simple', john)"));
        assertTrue(sql.contains("= u@x.com"));
        assertTrue(sql.contains("= 2026-01-02T03:04:05Z"));
        assertTrue(sql.contains("= admin"));
        assertTrue(sql.contains("like %:%"));

        assertEquals(2, joins.size());
        var clientJoin = joins.pollFirst();
        assertEquals(INNERJOIN, clientJoin.getType());
        assertEquals(ACTIVITY_LOG.getClientId(), clientJoin.getSourceId());
        assertEquals(CLIENT, clientJoin.getTargetEntity());
        assertEquals(CLIENT.getId(), clientJoin.getTargetId());

        var userJoin = joins.pollFirst();
        assertEquals(INNERJOIN, userJoin.getType());
        assertEquals(ACTIVITY_LOG.getUserId(), userJoin.getSourceId());
        assertEquals(USER, userJoin.getTargetEntity());
        assertEquals(USER.getId(), userJoin.getTargetId());
    }

    private static ClientPersistenceFilter clientByName(String term) {
        var client = new ClientPersistenceFilter();
        client.setName(new com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.PersonBaseFilter(
                new StringTermFullTextCondition(List.of(term), null, null, null), null, null, null, null, null
        ));
        return client;
    }

    private static UserPersistenceFilter userByEmail(String email) {
        var user = new UserPersistenceFilter();
        user.setEmail(new com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.EmailBaseFilter(
                new StringRegExpExactCondition(null, email, null), null, null, null
        ));
        return user;
    }

    private static DateTimeBaseFilter dateFilter(OffsetDateTime date) {
        return new DateTimeBaseFilter(
                new DateTimeCondition(date, null, null, null, null, null, null), null, null, null
        );
    }
}
