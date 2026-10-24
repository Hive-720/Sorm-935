package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.PaymentMethodFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.PaymentMethod;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.PaymentMethodService;
import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.dto.PaymentMethodSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.PaymentMethodFilter;
import com.grunvald.sorm.testutil.MapStructTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentMethodResolverTest {

    @Mock
    private PaymentMethodService paymentMethodService;

    private final FilterMappers filterMappers = new FilterMappers();

    private PaymentMethodResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new PaymentMethodResolver(paymentMethodService, filterMappers.paymentMethodMapper,
                MapStructTestFactory.paymentMethodResultMapper());
    }

    private PaymentMethodFilterInput sampleFilter() {
        PaymentMethodFilterInput nestedAnd = new PaymentMethodFilterInput(
                SampleFilters.dateTime(SampleFilters.D11), null, null, null);
        PaymentMethodFilterInput nestedOr = new PaymentMethodFilterInput(
                SampleFilters.dateTime(SampleFilters.D12), null, null, null);
        PaymentMethodFilterInput nestedNot = new PaymentMethodFilterInput(
                SampleFilters.dateTime(SampleFilters.D13), null, null, null);
        return new PaymentMethodFilterInput(
                SampleFilters.dateTime(SampleFilters.D01),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);
    }

    @Test
    void paymentMethods_mapsFilterAndPassesPaginationArgumentsInOrder() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod();
        model.setId(1L);
        model.setModule("paypal");
        model.setDisplayName("PayPal");
        when(paymentMethodService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new PaymentMethodSearchResult("cur-7", true, List.of(model))));

        StepVerifier.create(resolver.paymentMethods(sampleFilter(), 20, 30, "cur-7"))
                .assertNext(result -> {
                    assertEquals("cur-7", result.cursor());
                    assertTrue(result.hasNextPage());
                    assertEquals(1, result.result().size());
                    PaymentMethod first = result.result().get(0);
                    assertEquals(1L, first.id());
                    assertEquals("paypal", first.moduleName());
                    assertEquals("PayPal", first.displayName());
                })
                .verifyComplete();

        ArgumentCaptor<PaymentMethodFilter> filterCaptor = ArgumentCaptor.forClass(PaymentMethodFilter.class);
        verify(paymentMethodService).search(filterCaptor.capture(), eq("cur-7"), eq(20), eq(30));
        PaymentMethodFilter filter = filterCaptor.getValue();
        assertNotNull(filter.getUpdatedAt());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), filter.getUpdatedAt().getUtc().getEq());
        assertEquals(1, filter.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), filter.getAnd().get(0).getUpdatedAt().getUtc().getEq());
        assertEquals(1, filter.getOr().size());
        assertNotNull(filter.getNot());
    }

    @Test
    void paymentMethods_passesNullArgumentsThrough() {
        when(paymentMethodService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new PaymentMethodSearchResult(null, false, List.of())));

        StepVerifier.create(resolver.paymentMethods(null, null, null, null))
                .assertNext(result -> {
                    assertNull(result.cursor());
                    assertFalse(result.hasNextPage());
                    assertEquals(0, result.result().size());
                })
                .verifyComplete();

        verify(paymentMethodService).search(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void paymentMethods_propagatesServiceError() {
        when(paymentMethodService.search(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("payment method boom")));

        StepVerifier.create(resolver.paymentMethods(sampleFilter(), 0, 10, null))
                .expectErrorSatisfies(e -> assertEquals("payment method boom", e.getMessage()))
                .verify();
    }

    @Test
    void paymentMethods_emptyServiceResultCompletesEmpty() {
        when(paymentMethodService.search(any(), any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(resolver.paymentMethods(sampleFilter(), 0, 10, null))
                .verifyComplete();
    }
}
