package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ProductFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.filter.ProductFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.productMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingNestedAndOrNot() {
        ProductFilterInput nestedAnd = new ProductFilterInput(
                SampleFilters.dateTime(SampleFilters.D11), null, null, null);
        ProductFilterInput nestedOr = new ProductFilterInput(
                SampleFilters.dateTime(SampleFilters.D12), null, null, null);
        ProductFilterInput nestedNot = new ProductFilterInput(
                SampleFilters.dateTime(SampleFilters.D13), null, null, null);
        ProductFilterInput input = new ProductFilterInput(
                SampleFilters.dateTime(SampleFilters.D09),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);

        ProductFilter result = mappers.productMapper.map(input);

        assertNotNull(result.getUpdatedAt());
        assertEquals(OffsetDateTime.parse(SampleFilters.D09), result.getUpdatedAt().getUtc().getEq());

        assertEquals(1, result.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), result.getAnd().getFirst().getUpdatedAt().getUtc().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D12), result.getOr().getFirst().getUpdatedAt().getUtc().getEq());
        assertNotNull(result.getNot());
        assertEquals(OffsetDateTime.parse(SampleFilters.D13), result.getNot().getUpdatedAt().getUtc().getEq());
    }
}
