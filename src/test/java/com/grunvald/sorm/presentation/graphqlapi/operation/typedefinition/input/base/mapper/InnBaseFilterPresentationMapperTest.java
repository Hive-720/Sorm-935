package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.InnBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class InnBaseFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.innBaseMapper.map(null));
    }

    @Test
    void map_fillsInnAndAndOrNot() {
        InnBaseFilter nested = new InnBaseFilter(
                new StringTermExactFilter("nested-inn", null, "nested-eq", null),
                null, null, null);
        InnBaseFilter input = new InnBaseFilter(
                new StringTermExactFilter("inn-a,inn-b", "inn-any", "inn-eq", List.of("inn-in-1")),
                List.of(nested), List.of(nested), nested);

        com.grunvald.sorm.service.whmcsdata.filter.base.InnBaseFilter result = mappers.innBaseMapper.map(input);

        assertNotNull(result.getInn());
        assertEquals(List.of("inn-a", "inn-b"), result.getInn().getAllOfTerms());
        assertEquals(List.of("inn-any"), result.getInn().getAnyOfTerms());
        assertEquals("inn-eq", result.getInn().getEq());
        assertEquals(List.of("inn-in-1"), result.getInn().getIn());

        assertEquals(1, result.getAnd().size());
        assertEquals("nested-eq", result.getAnd().get(0).getInn().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals("nested-eq", result.getOr().get(0).getInn().getEq());
        assertNotNull(result.getNot());
        assertEquals("nested-eq", result.getNot().getInn().getEq());
    }

    @Test
    void map_nullFields_keepNulls() {
        com.grunvald.sorm.service.whmcsdata.filter.base.InnBaseFilter result =
                mappers.innBaseMapper.map(new InnBaseFilter(null, null, null, null));

        assertNull(result.getInn());
        assertNull(result.getAnd());
        assertNull(result.getOr());
        assertNull(result.getNot());
    }
}
