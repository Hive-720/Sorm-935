package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.EmailFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.EmailBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.filter.EmailFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.emailMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingNestedAndOrNot() {
        EmailBaseFilter to = new EmailBaseFilter(new StringRegExpExactFilter("to-re", "to@mail.com", null), null, null, null);
        EmailBaseFilter cc = new EmailBaseFilter(new StringRegExpExactFilter("cc-re", "cc@mail.com", null), null, null, null);
        EmailBaseFilter bcc = new EmailBaseFilter(new StringRegExpExactFilter("bcc-re", "bcc@mail.com", null), null, null, null);
        EmailFilterInput nestedAnd = new EmailFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D11), null, null, null, null, null);
        EmailFilterInput nestedOr = new EmailFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D12), null, null, null, null, null);
        EmailFilterInput nestedNot = new EmailFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D13), null, null, null, null, null);
        EmailFilterInput input = new EmailFilterInput(
                SampleFilters.client(),
                to, cc, bcc,
                SampleFilters.dateTime(SampleFilters.D01),
                SampleFilters.dateTime(SampleFilters.D02),
                SampleFilters.dateTime(SampleFilters.D03),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);

        EmailFilter result = mappers.emailMapper.map(input);

        assertNotNull(result.getClient());
        assertEquals("exact-eq", result.getClient().getName().getFirstName().getEq());

        assertEquals("to-re", result.getTo().getEmail().getRegexp());
        assertEquals("to@mail.com", result.getTo().getEmail().getEq());
        assertEquals("cc@mail.com", result.getCc().getEmail().getEq());
        assertEquals("bcc@mail.com", result.getBcc().getEmail().getEq());

        assertEquals(OffsetDateTime.parse(SampleFilters.D01), result.getDate().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), result.getCreatedAt().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D03), result.getUpdatedAt().getUtc().getEq());

        assertEquals(1, result.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), result.getAnd().get(0).getDate().getUtc().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D12), result.getOr().get(0).getDate().getUtc().getEq());
        assertNotNull(result.getNot());
        assertEquals(OffsetDateTime.parse(SampleFilters.D13), result.getNot().getDate().getUtc().getEq());
    }
}
