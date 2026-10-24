package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.filter.ClientFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.clientMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingNestedAndOrNot() {
        ClientFilterInput nested = new ClientFilterInput(
                null, null, null, null, null, null, null, null, null, null, null, null, null);
        ClientFilterInput input = new ClientFilterInput(
                SampleFilters.person(),
                SampleFilters.user(),
                SampleFilters.clientGroup(),
                SampleFilters.organizationInfo(),
                SampleFilters.inn(),
                SampleFilters.emailBase(),
                SampleFilters.msisdn(),
                SampleFilters.mcc(),
                SampleFilters.address(),
                SampleFilters.dateTime(SampleFilters.D01),
                List.of(nested), List.of(nested), nested);

        ClientFilter result = mappers.clientMapper.map(input);

        // name
        assertNotNull(result.getName());
        assertEquals("exact-eq", result.getName().getFirstName().getEq());
        assertEquals(List.of("alpha", "beta"), result.getName().getFullName().getAllOfTerms());
        assertEquals("last-eq", result.getName().getLastName().getEq());

        // userOwner
        assertNotNull(result.getUserOwner());
        assertEquals("exact-eq", result.getUserOwner().getName().getFirstName().getEq());
        assertEquals("eq@mail.com", result.getUserOwner().getEmail().getEmail().getEq());

        // clientGroup
        assertNotNull(result.getClientGroup());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), result.getClientGroup().getUpdatedAt().getUtc().getEq());

        // organizationInfo
        assertNotNull(result.getOrganizationInfo());
        assertEquals("Grunvald", result.getOrganizationInfo().getNameFull().getEq());
        assertEquals(List.of("Other"), result.getOrganizationInfo().getNameFull().getIn());

        // inn
        assertNotNull(result.getInn());
        assertEquals("inn-eq", result.getInn().getInn().getEq());

        // email
        assertNotNull(result.getEmail());
        assertEquals("eq@mail.com", result.getEmail().getEmail().getEq());

        // msisdn
        assertNotNull(result.getMsisdn());
        assertEquals("79990001122", result.getMsisdn().getMsisdn().getEq());

        // mcc
        assertNotNull(result.getMcc());
        assertEquals(250, result.getMcc().getMcc().getEq());
        assertEquals(List.of(251, 252), result.getMcc().getMcc().getIn());
        assertEquals(1, result.getMcc().getMcc().getBetween().getMin());
        assertEquals(999, result.getMcc().getMcc().getBetween().getMax());

        // address
        assertNotNull(result.getAddress());
        assertEquals("re.*", result.getAddress().getAddress().getRegexp());
        assertEquals("zip-1", result.getAddress().getZip().getEq());

        // createdAt
        assertNotNull(result.getCreatedAt());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), result.getCreatedAt().getUtc().getEq());

        // and/or/not
        assertEquals(1, result.getAnd().size());
        assertNull(result.getAnd().getFirst().getName());
        assertEquals(1, result.getOr().size());
        assertNull(result.getOr().getFirst().getName());
        assertNotNull(result.getNot());
        assertNull(result.getNot().getName());
    }
}
