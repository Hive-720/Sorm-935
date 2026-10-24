package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter;

import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginFilterTest {

    private static final StringPath COLUMN = Expressions.stringPath("col");

    @Test
    void allNulls_returnsNull() {
        assertNull(new LoginFilter(null, null, null, null).toPredicate(COLUMN));
    }

    @Test
    void login_appliesRegexpEqAndIn() {
        var predicate = new LoginFilter(
                new StringRegExpExactCondition("^[a-z].*", "user1", List.of("user2", "user3")),
                null, null, null
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col ~ ^[a-z].*"));
        assertTrue(sql.contains("col = user1"));
        assertTrue(sql.contains("col in [user2, user3]"));
    }

    @Test
    void and_or_not_combineNestedFilters() {
        var predicate = new LoginFilter(
                null,
                List.of(loginFilter("a"), loginFilter("b")),
                List.of(loginFilter("c"), loginFilter("d")),
                loginFilter("e")
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col = a && col = b"));
        assertTrue(sql.contains("col = c || col = d"));
        assertTrue(sql.contains("!(col = e)"));
    }

    private static LoginFilter loginFilter(String login) {
        return new LoginFilter(new StringRegExpExactCondition(null, login, null), null, null, null);
    }
}
