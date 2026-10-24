package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.service.whmcsdata.filter.condition.StringTermExactCondition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringTermExactFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.stringTermExactMapper.map(null));
    }

    @Test
    void map_fillsAllFields() {
        StringTermExactCondition result = mappers.stringTermExactMapper.map(
                new StringTermExactFilter("a, b ,,c", "d,e", "eq", List.of("in-1", "in-2")));

        assertEquals(List.of("a", "b", "c"), result.getAllOfTerms());
        assertEquals(List.of("d", "e"), result.getAnyOfTerms());
        assertEquals("eq", result.getEq());
        assertEquals(List.of("in-1", "in-2"), result.getIn());
    }

    @Test
    void map_nullAlloftermsAndAnyofterms_produceEmptyTermLists() {
        // фактическое поведение: commaSeparatedToList(null) возвращает пустой список
        StringTermExactCondition result = mappers.stringTermExactMapper.map(
                new StringTermExactFilter(null, null, null, null));

        assertTrue(result.getAllOfTerms().isEmpty());
        assertTrue(result.getAnyOfTerms().isEmpty());
        assertNull(result.getEq());
        assertNull(result.getIn());
    }

    @Test
    void map_nullFilterFields_keepNulls() {
        StringTermExactCondition result = mappers.stringTermExactMapper.map(
                new StringTermExactFilter("a", "b", null, null));

        assertNull(result.getEq());
        assertNull(result.getIn());
    }
}
