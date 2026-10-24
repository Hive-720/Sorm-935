package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.user;

import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.EmailBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.PersonBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermFullTextCondition;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.List;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.user.QUser.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserPersistenceFilterTest {

    @Test
    void allNulls_returnsNull() {
        var filter = new UserPersistenceFilter();
        var joins = new ArrayDeque<Join>();

        assertNull(filter.toPredicate(joins));
        assertTrue(joins.isEmpty());
        assertEquals(USER, filter.getQEntity());
    }

    @Test
    void nameAndEmail_applyConditions() {
        var filter = new UserPersistenceFilter();
        filter.setName(new PersonBaseFilter(
                new StringTermFullTextCondition(List.of("john"), null, null, null), null, null, null, null, null
        ));
        filter.setEmail(new EmailBaseFilter(new StringRegExpExactCondition(null, "a@b.com", null), null, null, null));

        var sql = filter.toPredicate(new ArrayDeque<Join>()).toString();

        assertTrue(sql.contains("to_tsquery('simple', john)"));
        assertTrue(sql.contains("= a@b.com"));
    }

    @Test
    void and_or_not_combineNestedFilters() {
        var filter = new UserPersistenceFilter();
        filter.setAnd(List.of(userByName("a"), userByName("b")));
        filter.setOr(List.of(userByName("c"), userByName("d")));
        filter.setNot(userByName("e"));

        var sql = filter.toPredicate(new ArrayDeque<Join>()).toString();

        assertTrue(sql.contains("to_tsquery('simple', a) && to_tsvector"));
        assertTrue(sql.contains("to_tsquery('simple', c) || to_tsvector"));
        assertTrue(sql.contains("!to_tsvector"));
        assertTrue(sql.contains("to_tsquery('simple', e)"));
    }

    private static UserPersistenceFilter userByName(String term) {
        var filter = new UserPersistenceFilter();
        filter.setName(new PersonBaseFilter(
                new StringTermFullTextCondition(List.of(term), null, null, null), null, null, null, null, null
        ));
        return filter;
    }
}
