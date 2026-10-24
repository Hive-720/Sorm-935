package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.AddressBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpFullTextFilter;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AddressBaseFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.addressBaseMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingAndOrNot() {
        AddressBaseFilter nested = new AddressBaseFilter(
                new StringRegExpFullTextFilter("nested-re", "nested-all", "nested-any"),
                null, null, null, null, null, null, null);
        AddressBaseFilter input = new AddressBaseFilter(
                new StringRegExpFullTextFilter("re.*", "all-text", "any-text"),
                new StringExactFilter("zip-1", List.of("zip-2", "zip-3")),
                new StringExactFilter("RU", null),
                new StringExactFilter("MSK", null),
                new StringExactFilter("Moscow", null),
                List.of(nested), List.of(nested), nested);

        com.grunvald.sorm.service.whmcsdata.filter.base.AddressBaseFilter result = mappers.addressBaseMapper.map(input);

        assertNotNull(result.getAddress());
        assertEquals("re.*", result.getAddress().getRegexp());
        assertEquals("all-text", result.getAddress().getAllOfText());
        assertEquals("any-text", result.getAddress().getAnyOfText());

        assertNotNull(result.getZip());
        assertEquals("zip-1", result.getZip().getEq());
        assertEquals(List.of("zip-2", "zip-3"), result.getZip().getIn());
        assertEquals("RU", result.getCountry().getEq());
        assertNull(result.getCountry().getIn());
        assertEquals("MSK", result.getRegion().getEq());
        assertEquals("Moscow", result.getCity().getEq());

        assertEquals(1, result.getAnd().size());
        assertEquals("nested-re", result.getAnd().get(0).getAddress().getRegexp());
        assertEquals(1, result.getOr().size());
        assertEquals("nested-re", result.getOr().get(0).getAddress().getRegexp());
        assertNotNull(result.getNot());
        assertEquals("nested-re", result.getNot().getAddress().getRegexp());
    }

    @Test
    void map_nullFields_keepNulls() {
        AddressBaseFilter input = new AddressBaseFilter(null, null, null, null, null, null, null, null);

        com.grunvald.sorm.service.whmcsdata.filter.base.AddressBaseFilter result = mappers.addressBaseMapper.map(input);

        assertNull(result.getAddress());
        assertNull(result.getZip());
        assertNull(result.getCountry());
        assertNull(result.getRegion());
        assertNull(result.getCity());
        assertNull(result.getAnd());
        assertNull(result.getOr());
        assertNull(result.getNot());
    }
}
