package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientProductFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.IpAddressBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.filter.ClientProductFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientProductFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.clientProductMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingNestedAndOrNot() {
        IpAddressBaseFilter assignedIpAddresses = new IpAddressBaseFilter(
                new StringRegExpExactFilter("192.*", "192.168.0.1", null), IpType.IPV6, null, null, null);
        ClientProductFilterInput nestedAnd = new ClientProductFilterInput(null, null, null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D11), null, null, null, null, null);
        ClientProductFilterInput nestedOr = new ClientProductFilterInput(null, null, null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D12), null, null, null, null, null);
        ClientProductFilterInput nestedNot = new ClientProductFilterInput(null, null, null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D13), null, null, null, null, null);
        ClientProductFilterInput input = new ClientProductFilterInput(
                SampleFilters.client(),
                SampleFilters.order(),
                SampleFilters.product(),
                SampleFilters.domainName(),
                SampleFilters.ipAddress(),
                assignedIpAddresses,
                SampleFilters.dateTime(SampleFilters.D01),
                SampleFilters.dateTime(SampleFilters.D02),
                SampleFilters.dateTime(SampleFilters.D03),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);

        ClientProductFilter result = mappers.clientProductMapper.map(input);

        assertNotNull(result.getClient());
        assertEquals("exact-eq", result.getClient().getName().getFirstName().getEq());

        // order
        assertNotNull(result.getOrder());
        assertEquals(OffsetDateTime.parse(SampleFilters.D08), result.getOrder().getDate().getUtc().getEq());
        assertNotNull(result.getOrder().getItems());
        assertEquals("a.ru", result.getOrder().getItems().getDomain().getDomainName().getEq());
        assertEquals(1, result.getOrder().getItems().getAnd().size());
        assertEquals("nested\\.com", result.getOrder().getItems().getAnd().get(0).getDomain().getDomainName().getRegexp());

        // product
        assertNotNull(result.getProduct());
        assertEquals(OffsetDateTime.parse(SampleFilters.D09), result.getProduct().getUpdatedAt().getUtc().getEq());

        // domain
        assertNotNull(result.getDomain());
        assertEquals("a.ru", result.getDomain().getDomainName().getEq());

        // dedicatedIpAddress / assignedIpAddresses
        assertNotNull(result.getDedicatedIpAddress());
        assertEquals("10.0.0.1", result.getDedicatedIpAddress().getIp().getEq());
        assertEquals(IpType.IPV4, result.getDedicatedIpAddress().getType());
        assertNotNull(result.getAssignedIpAddresses());
        assertEquals("192.168.0.1", result.getAssignedIpAddresses().getIp().getEq());
        assertEquals(IpType.IPV6, result.getAssignedIpAddresses().getType());

        // regDate / nextDueDate / lastUpdate
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), result.getRegDate().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), result.getNextDueDate().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D03), result.getLastUpdate().getUtc().getEq());

        // and/or/not
        assertEquals(1, result.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), result.getAnd().get(0).getRegDate().getUtc().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D12), result.getOr().get(0).getRegDate().getUtc().getEq());
        assertNotNull(result.getNot());
        assertEquals(OffsetDateTime.parse(SampleFilters.D13), result.getNot().getRegDate().getUtc().getEq());
    }
}
