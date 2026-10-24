package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client;

import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.*;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.*;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.user.UserPersistenceFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayDeque;
import java.util.List;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClientGroup.CLIENT_GROUP;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.user.QUser.USER;
import static com.querydsl.core.JoinType.INNERJOIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientPersistenceFilterTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    @Test
    void allNulls_returnsNull() {
        var filter = new ClientPersistenceFilter();
        var joins = new ArrayDeque<Join>();

        assertNull(filter.toPredicate(joins));
        assertTrue(joins.isEmpty());
        assertEquals(CLIENT, filter.getQEntity());
    }

    @Test
    void allFields_addConditionsAndJoins() {
        var filter = new ClientPersistenceFilter();
        filter.setUserOwner(userByEmail("o@x.com"));
        var clientGroup = new ClientGroupPersistenceFilter();
        clientGroup.setUpdatedAt(dateFilter(DATE));
        filter.setClientGroup(clientGroup);
        filter.setName(new PersonBaseFilter(
                new StringTermFullTextCondition(List.of("john"), null, null, null),
                new StringTermExactCondition(null, null, "Alice", null),
                new StringTermExactCondition(null, null, "Smith", null),
                null, null, null
        ));
        filter.setOrganizationInfo(new OrganizationInfoBaseFilter(
                new StringRegExpExactCondition(null, "ООО Ромашка", null), null, null, null
        ));
        filter.setInn(new InnBaseFilter(new StringTermExactCondition(null, null, "7711223344", null), null, null, null));
        filter.setEmail(new EmailBaseFilter(new StringRegExpExactCondition(null, "a@b.com", null), null, null, null));
        filter.setMsisdn(new MsisdnBaseFilter(new StringRegExpExactCondition(null, "79991234567", null), null, null, null));
        filter.setMcc(new MccBaseFilter(new IntCondition(123, null, null, null, null, null, null), null, null, null));
        filter.setAddress(new AddressBaseFilter(
                null, new StringExactCondition("10001", null), new StringExactCondition("USA", null),
                null, null, null, null, null
        ));

        var joins = new ArrayDeque<Join>();
        var sql = filter.toPredicate(joins).toString();

        assertTrue(sql.contains("= o@x.com"));
        assertTrue(sql.contains("= 2026-01-02T03:04:05Z"));
        assertTrue(sql.contains("to_tsquery('simple', john)"));
        assertTrue(sql.contains("= Alice"));
        assertTrue(sql.contains("= Smith"));
        assertTrue(sql.contains("= ООО Ромашка"));
        assertTrue(sql.contains("= 7711223344"));
        assertTrue(sql.contains("= a@b.com"));
        assertTrue(sql.contains("= 79991234567"));
        assertTrue(sql.contains("= 123"));
        assertTrue(sql.contains("= 10001"));
        assertTrue(sql.contains("= USA"));

        assertEquals(2, joins.size());
        var userJoin = joins.pollFirst();
        assertEquals(INNERJOIN, userJoin.getType());
        assertEquals(CLIENT.getOwnerUserId(), userJoin.getSourceId());
        assertEquals(USER, userJoin.getTargetEntity());

        var groupJoin = joins.pollFirst();
        assertEquals(INNERJOIN, groupJoin.getType());
        assertEquals(CLIENT.getClientGroupId(), groupJoin.getSourceId());
        assertEquals(CLIENT_GROUP, groupJoin.getTargetEntity());
    }

    private static UserPersistenceFilter userByEmail(String email) {
        var user = new UserPersistenceFilter();
        user.setEmail(new EmailBaseFilter(new StringRegExpExactCondition(null, email, null), null, null, null));
        return user;
    }

    private static DateTimeBaseFilter dateFilter(OffsetDateTime date) {
        return new DateTimeBaseFilter(
                new DateTimeCondition(date, null, null, null, null, null, null), null, null, null
        );
    }
}
