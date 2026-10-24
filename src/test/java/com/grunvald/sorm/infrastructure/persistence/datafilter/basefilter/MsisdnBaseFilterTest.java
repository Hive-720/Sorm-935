package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter;

import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MsisdnBaseFilterTest {

    private static final StringPath COLUMN = Expressions.stringPath("col");

    @Test
    void allNulls_returnsNull() {
        assertNull(new MsisdnBaseFilter(null, null, null, null).toPredicate(COLUMN));
    }

    @Test
    void msisdn_appliesRegexpAndEq() {
        var predicate = new MsisdnBaseFilter(
                new StringRegExpExactCondition("^79.*", "79991234567", List.of("79998765432")),
                null, null, null
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col ~ ^79.*"));
        assertTrue(sql.contains("col = 79991234567"));
        assertTrue(sql.contains("col = 79998765432"));
    }

    @Test
    void and_or_not_combineNestedFilters() {
        var predicate = new MsisdnBaseFilter(
                null,
                List.of(msisdnFilter("1"), msisdnFilter("2")),
                List.of(msisdnFilter("3"), msisdnFilter("4")),
                msisdnFilter("5")
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col = 1 && col = 2"));
        assertTrue(sql.contains("col = 3 || col = 4"));
        assertTrue(sql.contains("!(col = 5)"));
    }

    private static MsisdnBaseFilter msisdnFilter(String msisdn) {
        return new MsisdnBaseFilter(new StringRegExpExactCondition(null, msisdn, null), null, null, null);
    }
}
