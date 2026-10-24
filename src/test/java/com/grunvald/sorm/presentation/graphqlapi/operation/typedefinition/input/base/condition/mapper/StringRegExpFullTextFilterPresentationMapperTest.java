package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpFullTextFilter;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.service.whmcsdata.filter.condition.StringRegExpFullTextCondition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringRegExpFullTextFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.stringRegExpFullTextMapper.map(null));
    }

    @Test
    void map_fillsAllFields() {
        StringRegExpFullTextCondition result = mappers.stringRegExpFullTextMapper.map(
                new StringRegExpFullTextFilter("re.*", "all-text", "any-text"));

        assertEquals("re.*", result.getRegexp());
        assertEquals("all-text", result.getAllOfText());
        assertEquals("any-text", result.getAnyOfText());
    }

    @Test
    void map_nullFields_keepNulls() {
        StringRegExpFullTextCondition result = mappers.stringRegExpFullTextMapper.map(
                new StringRegExpFullTextFilter(null, null, null));

        assertNull(result.getRegexp());
        assertNull(result.getAllOfText());
        assertNull(result.getAnyOfText());
    }
}
