package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.UserFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.EmailBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.filter.UserFilter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.userMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingNestedAndOrNot() {
        EmailBaseFilter andEmail = new EmailBaseFilter(
                new StringRegExpExactFilter("and-re", "and@mail.com", null), null, null, null);
        EmailBaseFilter orEmail = new EmailBaseFilter(
                new StringRegExpExactFilter("or-re", "or@mail.com", null), null, null, null);
        EmailBaseFilter notEmail = new EmailBaseFilter(
                new StringRegExpExactFilter("not-re", "not@mail.com", null), null, null, null);
        UserFilterInput nestedAnd = new UserFilterInput(null, andEmail, null, null, null);
        UserFilterInput nestedOr = new UserFilterInput(null, orEmail, null, null, null);
        UserFilterInput nestedNot = new UserFilterInput(null, notEmail, null, null, null);
        UserFilterInput input = new UserFilterInput(
                SampleFilters.person(),
                SampleFilters.emailBase(),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);

        UserFilter result = mappers.userMapper.map(input);

        assertNotNull(result.getName());
        assertEquals("exact-eq", result.getName().getFirstName().getEq());
        assertEquals(List.of("alpha", "beta"), result.getName().getFullName().getAllOfTerms());
        assertEquals("last-eq", result.getName().getLastName().getEq());

        assertNotNull(result.getEmail());
        assertEquals("eq@mail.com", result.getEmail().getEmail().getEq());

        assertEquals(1, result.getAnd().size());
        assertEquals("and@mail.com", result.getAnd().getFirst().getEmail().getEmail().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals("or@mail.com", result.getOr().getFirst().getEmail().getEmail().getEq());
        assertNotNull(result.getNot());
        assertEquals("not@mail.com", result.getNot().getEmail().getEmail().getEq());
    }
}
