package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ActivityLogFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.filter.ActivityLogFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ActivityLogFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.activityLogMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingNestedAndOrNot() {
        ActivityLogFilterInput nestedAnd = new ActivityLogFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D11), null, null, null);
        ActivityLogFilterInput nestedOr = new ActivityLogFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D12), null, null, null);
        ActivityLogFilterInput nestedNot = new ActivityLogFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D13), null, null, null);
        ActivityLogFilterInput input = new ActivityLogFilterInput(
                SampleFilters.client(),
                SampleFilters.user(),
                SampleFilters.loginBase(),
                SampleFilters.ipAddress(),
                SampleFilters.dateTime(SampleFilters.D10),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);

        ActivityLogFilter result = mappers.activityLogMapper.map(input);

        assertNotNull(result.getClient());
        assertEquals("exact-eq", result.getClient().getName().getFirstName().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), result.getClient().getCreatedAt().getUtc().getEq());

        assertNotNull(result.getUser());
        assertEquals("exact-eq", result.getUser().getName().getFirstName().getEq());
        assertEquals("eq@mail.com", result.getUser().getEmail().getEmail().getEq());

        assertNotNull(result.getUsername());
        assertEquals("user.*", result.getUsername().getLogin().getRegexp());
        assertEquals("user-1", result.getUsername().getLogin().getEq());
        assertEquals(List.of("user-2"), result.getUsername().getLogin().getIn());

        assertNotNull(result.getIpAddress());
        assertEquals("10.0.0.1", result.getIpAddress().getIp().getEq());
        assertEquals(com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType.IPV4,
                result.getIpAddress().getType());

        assertNotNull(result.getDate());
        assertEquals(OffsetDateTime.parse(SampleFilters.D10), result.getDate().getUtc().getEq());

        assertEquals(1, result.getAnd().size());
        assertNull(result.getAnd().get(0).getClient());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), result.getAnd().get(0).getDate().getUtc().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D12), result.getOr().get(0).getDate().getUtc().getEq());
        assertNotNull(result.getNot());
        assertEquals(OffsetDateTime.parse(SampleFilters.D13), result.getNot().getDate().getUtc().getEq());
    }
}
