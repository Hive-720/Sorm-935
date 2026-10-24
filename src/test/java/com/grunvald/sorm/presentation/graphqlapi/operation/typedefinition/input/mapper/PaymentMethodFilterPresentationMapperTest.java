package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.PaymentMethodFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.filter.PaymentMethodFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentMethodFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.paymentMethodMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingNestedAndOrNot() {
        PaymentMethodFilterInput nestedAnd = new PaymentMethodFilterInput(
                SampleFilters.dateTime(SampleFilters.D11), null, null, null);
        PaymentMethodFilterInput nestedOr = new PaymentMethodFilterInput(
                SampleFilters.dateTime(SampleFilters.D12), null, null, null);
        PaymentMethodFilterInput nestedNot = new PaymentMethodFilterInput(
                SampleFilters.dateTime(SampleFilters.D13), null, null, null);
        PaymentMethodFilterInput input = new PaymentMethodFilterInput(
                SampleFilters.dateTime(SampleFilters.D01),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);

        PaymentMethodFilter result = mappers.paymentMethodMapper.map(input);

        assertNotNull(result.getUpdatedAt());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), result.getUpdatedAt().getUtc().getEq());

        assertEquals(1, result.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), result.getAnd().getFirst().getUpdatedAt().getUtc().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D12), result.getOr().getFirst().getUpdatedAt().getUtc().getEq());
        assertNotNull(result.getNot());
        assertEquals(OffsetDateTime.parse(SampleFilters.D13), result.getNot().getUpdatedAt().getUtc().getEq());
    }
}
