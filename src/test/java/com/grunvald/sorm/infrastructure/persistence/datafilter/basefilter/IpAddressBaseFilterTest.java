package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter;

import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;
import com.querydsl.core.types.dsl.ArrayPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IpAddressBaseFilterTest {

    private static final StringPath STRING_COLUMN = Expressions.stringPath("col");
    private static final ArrayPath<String, String> ARRAY_COLUMN = Expressions.arrayPath(String.class, "col");

    @Test
    void allNulls_returnsNull() {
        assertNull(new IpAddressBaseFilter(null, null, null, null, null).toPredicate(STRING_COLUMN));
        assertNull(new IpAddressBaseFilter(null, null, null, null, null).toPredicate(ARRAY_COLUMN));
    }

    @Test
    void arrayColumn_ipv4_usesNotLikeAll() {
        var predicate = new IpAddressBaseFilter(
                new StringRegExpExactCondition("^10\\.", null, null), IpType.IPV4, null, null, null
        ).toPredicate(ARRAY_COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("10\\. ~ ANY (col)"));
        assertTrue(sql.contains("%:% LIKE ALL"));
    }

    @Test
    void arrayColumn_ipv6_usesLikeAny() {
        var predicate = new IpAddressBaseFilter(null, IpType.IPV6, null, null, null).toPredicate(ARRAY_COLUMN);

        assertTrue(predicate.toString().contains("%:% LIKE ANY"));
    }

    @Test
    void stringColumn_ipv4_usesNotLike() {
        var predicate = new IpAddressBaseFilter(null, IpType.IPV4, null, null, null).toPredicate(STRING_COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("!(col like %:%)"));
        assertTrue(sql.contains("%:%"));
    }

    @Test
    void stringColumn_ipv6_usesLike() {
        var predicate = new IpAddressBaseFilter(null, IpType.IPV6, null, null, null).toPredicate(STRING_COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("like"));
        assertTrue(sql.contains("%:%"));
    }

    @Test
    void and_or_not_combineNestedFilters() {
        var predicate = new IpAddressBaseFilter(
                null, null,
                List.of(ipFilter("10.0.0.1"), ipFilter("10.0.0.2")),
                List.of(ipFilter("10.0.0.3"), ipFilter("10.0.0.4")),
                ipFilter("10.0.0.5")
        ).toPredicate(ARRAY_COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("10.0.0.1 = ANY (col) && 10.0.0.2 = ANY (col)"));
        assertTrue(sql.contains("10.0.0.3 = ANY (col) || 10.0.0.4 = ANY (col)"));
        assertTrue(sql.contains("!10.0.0.5 = ANY (col)"));
    }

    private static IpAddressBaseFilter ipFilter(String ip) {
        return new IpAddressBaseFilter(new StringRegExpExactCondition(null, ip, null), null, null, null, null);
    }
}
