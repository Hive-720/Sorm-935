package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ContactFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.filter.ContactFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContactFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.contactMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingNestedAndOrNot() {
        ContactFilterInput nestedAnd = new ContactFilterInput(null, null, null, null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D11), null, null, null, null);
        ContactFilterInput nestedOr = new ContactFilterInput(null, null, null, null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D12), null, null, null, null);
        ContactFilterInput nestedNot = new ContactFilterInput(null, null, null, null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D13), null, null, null, null);
        ContactFilterInput input = new ContactFilterInput(
                SampleFilters.client(),
                SampleFilters.person(),
                SampleFilters.organizationInfo(),
                SampleFilters.emailBase(),
                SampleFilters.address(),
                SampleFilters.inn(),
                SampleFilters.msisdn(),
                SampleFilters.dateTime(SampleFilters.D01),
                SampleFilters.dateTime(SampleFilters.D02),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);

        ContactFilter result = mappers.contactMapper.map(input);

        assertNotNull(result.getClient());
        assertEquals("exact-eq", result.getClient().getName().getFirstName().getEq());

        assertNotNull(result.getName());
        assertEquals("exact-eq", result.getName().getFirstName().getEq());
        assertEquals(List.of("alpha", "beta"), result.getName().getFullName().getAllOfTerms());

        assertNotNull(result.getOrganizationInfo());
        assertEquals("Grunvald", result.getOrganizationInfo().getNameFull().getEq());

        assertNotNull(result.getEmail());
        assertEquals("eq@mail.com", result.getEmail().getEmail().getEq());

        assertNotNull(result.getAddress());
        assertEquals("re.*", result.getAddress().getAddress().getRegexp());
        assertEquals("zip-1", result.getAddress().getZip().getEq());

        assertNotNull(result.getInn());
        assertEquals("inn-eq", result.getInn().getInn().getEq());

        assertNotNull(result.getMsisdn());
        assertEquals("79990001122", result.getMsisdn().getMsisdn().getEq());

        assertEquals(OffsetDateTime.parse(SampleFilters.D01), result.getUpdatedAt().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), result.getCreatedAt().getUtc().getEq());

        assertEquals(1, result.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), result.getAnd().getFirst().getUpdatedAt().getUtc().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D12), result.getOr().getFirst().getUpdatedAt().getUtc().getEq());
        assertNotNull(result.getNot());
        assertEquals(OffsetDateTime.parse(SampleFilters.D13), result.getNot().getUpdatedAt().getUtc().getEq());
    }
}
