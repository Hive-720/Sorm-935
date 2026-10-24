package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.PersonBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermFullTextFilter;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonBaseFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.personBaseMapper.map(null));
    }

    @Test
    void map_nameIsMappedToFirstName() {
        PersonBaseFilter nested = new PersonBaseFilter(
                null,
                new StringTermExactFilter("nested-exact", null, "nested-eq", null),
                null, null, null, null);
        PersonBaseFilter input = new PersonBaseFilter(
                new StringTermFullTextFilter("alpha,beta", "gamma", "all-text", "any-text"),
                new StringTermExactFilter("exact-a,exact-b", "exact-any", "exact-eq", List.of("in-1")),
                new StringTermExactFilter("last-a", null, "last-eq", null),
                List.of(nested), List.of(nested), nested);

        com.grunvald.sorm.service.whmcsdata.filter.base.PersonBaseFilter result = mappers.personBaseMapper.map(input);

        // fullName -> fullName
        assertNotNull(result.getFullName());
        assertEquals(List.of("alpha", "beta"), result.getFullName().getAllOfTerms());
        assertEquals(List.of("gamma"), result.getFullName().getAnyOfTerms());
        assertEquals("all-text", result.getFullName().getAllOfText());
        assertEquals("any-text", result.getFullName().getAnyOfText());

        // name -> firstName (специальный маппинг @Mapping(target = "firstName", source = "name"))
        assertNotNull(result.getFirstName());
        assertEquals(List.of("exact-a", "exact-b"), result.getFirstName().getAllOfTerms());
        assertEquals(List.of("exact-any"), result.getFirstName().getAnyOfTerms());
        assertEquals("exact-eq", result.getFirstName().getEq());
        assertEquals(List.of("in-1"), result.getFirstName().getIn());

        // lastName -> lastName
        assertNotNull(result.getLastName());
        assertEquals(List.of("last-a"), result.getLastName().getAllOfTerms());
        assertEquals("last-eq", result.getLastName().getEq());
        assertTrue(result.getLastName().getAnyOfTerms().isEmpty());

        // and/or/not
        assertEquals(1, result.getAnd().size());
        assertEquals("nested-eq", result.getAnd().getFirst().getFirstName().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals("nested-eq", result.getOr().getFirst().getFirstName().getEq());
        assertNotNull(result.getNot());
        assertEquals("nested-eq", result.getNot().getFirstName().getEq());
    }

    @Test
    void map_nullFields_keepNulls() {
        com.grunvald.sorm.service.whmcsdata.filter.base.PersonBaseFilter result =
                mappers.personBaseMapper.map(new PersonBaseFilter(null, null, null, null, null, null));

        assertNull(result.getFullName());
        assertNull(result.getFirstName());
        assertNull(result.getLastName());
        assertNull(result.getAnd());
        assertNull(result.getOr());
        assertNull(result.getNot());
    }
}
