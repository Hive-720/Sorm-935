package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientLoginFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.filter.ClientLoginFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientLoginFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.clientLoginMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingNestedAndOrNot() {
        ClientLoginFilterInput nestedAnd = new ClientLoginFilterInput(null, null,
                SampleFilters.dateTime(SampleFilters.D11), null, null, null);
        ClientLoginFilterInput nestedOr = new ClientLoginFilterInput(null, null,
                SampleFilters.dateTime(SampleFilters.D12), null, null, null);
        ClientLoginFilterInput nestedNot = new ClientLoginFilterInput(null, null,
                SampleFilters.dateTime(SampleFilters.D13), null, null, null);
        ClientLoginFilterInput input = new ClientLoginFilterInput(
                SampleFilters.client(),
                SampleFilters.ipAddress(),
                SampleFilters.dateTime(SampleFilters.D10),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);

        ClientLoginFilter result = mappers.clientLoginMapper.map(input);

        assertNotNull(result.getClient());
        assertEquals("exact-eq", result.getClient().getName().getFirstName().getEq());

        assertNotNull(result.getIpAddress());
        assertEquals("10.0.0.1", result.getIpAddress().getIp().getEq());
        assertEquals(IpType.IPV4, result.getIpAddress().getType());

        assertNotNull(result.getDate());
        assertEquals(OffsetDateTime.parse(SampleFilters.D10), result.getDate().getUtc().getEq());

        assertEquals(1, result.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), result.getAnd().get(0).getDate().getUtc().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D12), result.getOr().get(0).getDate().getUtc().getEq());
        assertNotNull(result.getNot());
        assertEquals(OffsetDateTime.parse(SampleFilters.D13), result.getNot().getDate().getUtc().getEq());
    }
}
