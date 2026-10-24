package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter;

import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrganizationInfoBaseFilterTest {

    private static final StringPath COLUMN = Expressions.stringPath("col");

    @Test
    void allNulls_returnsNull() {
        assertNull(new OrganizationInfoBaseFilter(null, null, null, null).toPredicate(COLUMN));
    }

    @Test
    void nameFull_appliesRegexpAndEq() {
        var predicate = new OrganizationInfoBaseFilter(
                new StringRegExpExactCondition("ООО.*", "ООО Ромашка", List.of("ООО Берёзка")),
                null, null, null
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col ~ ООО.*"));
        assertTrue(sql.contains("col = ООО Ромашка"));
        assertTrue(sql.contains("col = ООО Берёзка"));
    }

    @Test
    void and_or_not_combineNestedFilters() {
        var predicate = new OrganizationInfoBaseFilter(
                null,
                List.of(nameFilter("a"), nameFilter("b")),
                List.of(nameFilter("c"), nameFilter("d")),
                nameFilter("e")
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col = a && col = b"));
        assertTrue(sql.contains("col = c || col = d"));
        assertTrue(sql.contains("!(col = e)"));
    }

    private static OrganizationInfoBaseFilter nameFilter(String name) {
        return new OrganizationInfoBaseFilter(new StringRegExpExactCondition(null, name, null), null, null, null);
    }
}
