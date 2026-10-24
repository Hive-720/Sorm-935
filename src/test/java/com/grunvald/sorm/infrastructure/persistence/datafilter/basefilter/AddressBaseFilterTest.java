package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter;

import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringExactCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpFullTextCondition;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddressBaseFilterTest {

    private static final StringPath ADDRESS = Expressions.stringPath("addr");
    private static final StringPath ZIP = Expressions.stringPath("zip");
    private static final StringPath COUNTRY = Expressions.stringPath("country");
    private static final StringPath REGION = Expressions.stringPath("region");
    private static final StringPath CITY = Expressions.stringPath("city");

    @Test
    void allNulls_returnsNull() {
        assertNull(new AddressBaseFilter(null, null, null, null, null, null, null, null)
                .toPredicate(ADDRESS, ZIP, COUNTRY, REGION, CITY));
    }

    @Test
    void allFields_bindEachConditionToItsColumn() {
        var predicate = new AddressBaseFilter(
                new StringRegExpFullTextCondition("^[A-Z].*", "main street", null),
                new StringExactCondition("10001", List.of("10002", "10003")),
                new StringExactCondition("USA", null),
                new StringExactCondition("NY", null),
                new StringExactCondition("NYC", null),
                null, null, null
        ).toPredicate(ADDRESS, ZIP, COUNTRY, REGION, CITY);

        var sql = predicate.toString();
        assertTrue(sql.contains("addr ~ ^[A-Z].*"));
        assertTrue(sql.contains("ILIKE ALL"));
        assertTrue(sql.contains("%main%"));
        assertTrue(sql.contains("zip = 10001"));
        assertTrue(sql.contains("zip in [10002, 10003]"));
        assertTrue(sql.contains("country = USA"));
        assertTrue(sql.contains("region = NY"));
        assertTrue(sql.contains("city = NYC"));
    }

    @Test
    void and_or_not_combineNestedFilters() {
        var predicate = new AddressBaseFilter(
                null, null, null, null, null,
                List.of(zipFilter("1"), zipFilter("2")),
                List.of(zipFilter("3"), zipFilter("4")),
                zipFilter("5")
        ).toPredicate(ADDRESS, ZIP, COUNTRY, REGION, CITY);

        var sql = predicate.toString();
        assertTrue(sql.contains("zip = 1 && zip = 2"));
        assertTrue(sql.contains("zip = 3 || zip = 4"));
        assertTrue(sql.contains("!(zip = 5)"));
    }

    private static AddressBaseFilter zipFilter(String value) {
        return new AddressBaseFilter(null, new StringExactCondition(value, null), null, null, null, null, null, null);
    }
}
