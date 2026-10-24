package com.grunvald.sorm.presentation.graphqlapi.util;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermFullTextFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.StringTermExactFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.StringTermFullTextFilterPresentationMapperImpl;
import com.grunvald.sorm.service.whmcsdata.filter.condition.StringTermExactCondition;
import com.grunvald.sorm.service.whmcsdata.filter.condition.StringTermFullTextCondition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapperListConverterTest {

    private final StringTermExactFilterPresentationMapperImpl exactMapper = new StringTermExactFilterPresentationMapperImpl();
    private final StringTermFullTextFilterPresentationMapperImpl fullTextMapper = new StringTermFullTextFilterPresentationMapperImpl();

    @Test
    void commaSeparatedToList_blank_returnsEmptyList() {
        assertTrue(exactMapper.commaSeparatedToList(null).isEmpty());
        assertTrue(exactMapper.commaSeparatedToList("").isEmpty());
        assertTrue(exactMapper.commaSeparatedToList("   ").isEmpty());
    }

    @Test
    void commaSeparatedToList_splitsTrimsAndFiltersBlank() {
        assertEquals(List.of("a", "b", "c"), exactMapper.commaSeparatedToList("a, b ,,c"));
    }

    @Test
    void commaSeparatedToList_singleElement() {
        assertEquals(List.of("only"), exactMapper.commaSeparatedToList("  only  "));
    }

    @Test
    void exactMapper_usesCommaSeparatedToList() {
        StringTermExactCondition result = exactMapper.map(
                new StringTermExactFilter("a, b ,,c", "d,e", "eq", List.of("in-1")));

        assertEquals(List.of("a", "b", "c"), result.getAllOfTerms());
        assertEquals(List.of("d", "e"), result.getAnyOfTerms());
        assertEquals("eq", result.getEq());
        assertEquals(List.of("in-1"), result.getIn());
    }

    @Test
    void fullTextMapper_usesCommaSeparatedToList() {
        StringTermFullTextCondition result = fullTextMapper.map(
                new StringTermFullTextFilter("a,,b", "", "all-text", "any-text"));

        assertEquals(List.of("a", "b"), result.getAllOfTerms());
        assertTrue(result.getAnyOfTerms().isEmpty());
        assertEquals("all-text", result.getAllOfText());
        assertEquals("any-text", result.getAnyOfText());
    }
}
