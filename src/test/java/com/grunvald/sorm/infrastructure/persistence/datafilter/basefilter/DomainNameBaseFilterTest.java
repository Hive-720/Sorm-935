package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter;

import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainNameBaseFilterTest {

    private static final StringPath COLUMN = Expressions.stringPath("col");

    @Test
    void allNulls_returnsNull() {
        assertNull(new DomainNameBaseFilter(null, null, null, null).toPredicate(COLUMN));
    }

    @Test
    void domainName_appliesRegexpEqAndIn() {
        var predicate = new DomainNameBaseFilter(
                new StringRegExpExactCondition("^www\\.", "example.com", List.of("example.org", "example.net")),
                null, null, null
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col ~ ^www\\."));
        assertTrue(sql.contains("col = example.com"));
        assertTrue(sql.contains("col in [example.org, example.net]"));
    }

    @Test
    void and_or_not_combineNestedFilters() {
        var predicate = new DomainNameBaseFilter(
                null,
                List.of(domainFilter("a.com"), domainFilter("b.com")),
                List.of(domainFilter("c.com"), domainFilter("d.com")),
                domainFilter("e.com")
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col = a.com && col = b.com"));
        assertTrue(sql.contains("col = c.com || col = d.com"));
        assertTrue(sql.contains("!(col = e.com)"));
    }

    private static DomainNameBaseFilter domainFilter(String domain) {
        return new DomainNameBaseFilter(new StringRegExpExactCondition(null, domain, null), null, null, null);
    }
}
