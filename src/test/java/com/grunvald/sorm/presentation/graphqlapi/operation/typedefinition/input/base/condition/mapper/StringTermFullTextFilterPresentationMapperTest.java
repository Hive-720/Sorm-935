package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermFullTextFilter;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.service.whmcsdata.filter.condition.StringTermFullTextCondition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringTermFullTextFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.stringTermFullTextMapper.map(null));
    }

    @Test
    void map_fillsAllFields() {
        StringTermFullTextCondition result = mappers.stringTermFullTextMapper.map(
                new StringTermFullTextFilter("a, b ,,c", "d,e", "all-text", "any-text"));

        assertEquals(List.of("a", "b", "c"), result.getAllOfTerms());
        assertEquals(List.of("d", "e"), result.getAnyOfTerms());
        assertEquals("all-text", result.getAllOfText());
        assertEquals("any-text", result.getAnyOfText());
    }

    @Test
    void map_nullAlloftermsAndAnyofterms_produceEmptyTermLists() {
        // фактическое поведение: commaSeparatedToList(null) возвращает пустой список
        StringTermFullTextCondition result = mappers.stringTermFullTextMapper.map(
                new StringTermFullTextFilter(null, null, "all-text", null));

        assertTrue(result.getAllOfTerms().isEmpty());
        assertTrue(result.getAnyOfTerms().isEmpty());
        assertEquals("all-text", result.getAllOfText());
        assertNull(result.getAnyOfText());
    }
}
