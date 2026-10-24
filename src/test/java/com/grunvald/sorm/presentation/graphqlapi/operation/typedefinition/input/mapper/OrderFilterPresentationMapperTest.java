package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.OrderFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.filter.OrderFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.orderMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingNestedAndOrNot() {
        OrderFilterInput nestedAnd = new OrderFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D11), null, null, null);
        OrderFilterInput nestedOr = new OrderFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D12), null, null, null);
        OrderFilterInput nestedNot = new OrderFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D13), null, null, null);
        OrderFilterInput input = new OrderFilterInput(
                SampleFilters.client(),
                SampleFilters.invoice(),
                SampleFilters.ipAddress(),
                SampleFilters.orderItem(),
                SampleFilters.dateTime(SampleFilters.D08),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);

        OrderFilter result = mappers.orderMapper.map(input);

        assertNotNull(result.getClient());
        assertEquals("exact-eq", result.getClient().getName().getFirstName().getEq());

        // invoice
        assertNotNull(result.getInvoice());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), result.getInvoice().getDate().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), result.getInvoice().getDueDate().getUtc().getEq());

        // items
        assertNotNull(result.getItems());
        assertEquals("a.ru", result.getItems().getDomain().getDomainName().getEq());
        assertEquals(1, result.getItems().getAnd().size());
        assertEquals("nested\\.com", result.getItems().getAnd().get(0).getDomain().getDomainName().getRegexp());
        assertEquals(1, result.getItems().getOr().size());
        assertNotNull(result.getItems().getNot());

        // ipAddress
        assertNotNull(result.getIpAddress());
        assertEquals("10.0.0.1", result.getIpAddress().getIp().getEq());
        assertEquals(IpType.IPV4, result.getIpAddress().getType());

        // date
        assertEquals(OffsetDateTime.parse(SampleFilters.D08), result.getDate().getUtc().getEq());

        // and/or/not
        assertEquals(1, result.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), result.getAnd().get(0).getDate().getUtc().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D12), result.getOr().get(0).getDate().getUtc().getEq());
        assertNotNull(result.getNot());
        assertEquals(OffsetDateTime.parse(SampleFilters.D13), result.getNot().getDate().getUtc().getEq());
    }
}
