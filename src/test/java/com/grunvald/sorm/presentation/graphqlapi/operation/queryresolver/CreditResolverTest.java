package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.CreditFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Credit;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.dataservice.credit.CreditService;
import com.grunvald.sorm.service.whmcsdata.dataservice.credit.dto.CreditSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.CreditFilter;
import com.grunvald.sorm.testutil.MapStructTestFactory;
import org.dataloader.DataLoader;
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
import java.util.concurrent.CompletableFuture;

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
class CreditResolverTest {

    @Mock
    private CreditService creditService;

    @Mock
    private DataLoader<Long, Client> clientDataLoader;

    private final FilterMappers filterMappers = new FilterMappers();

    private CreditResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new CreditResolver(creditService, filterMappers.creditMapper,
                MapStructTestFactory.creditResultMapper());
    }

    private CreditFilterInput sampleFilter() {
        CreditFilterInput nestedAnd = new CreditFilterInput(null, SampleFilters.dateTime(SampleFilters.D11),
                null, null, null);
        CreditFilterInput nestedOr = new CreditFilterInput(null, SampleFilters.dateTime(SampleFilters.D12),
                null, null, null);
        CreditFilterInput nestedNot = new CreditFilterInput(null, SampleFilters.dateTime(SampleFilters.D13),
                null, null, null);
        return new CreditFilterInput(
                SampleFilters.client(),
                SampleFilters.dateTime(SampleFilters.D10),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);
    }

    @Test
    void credits_mapsFilterAndPassesPaginationArgumentsInOrder() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit();
        model.setId(42L);
        model.setClientId(7L);
        model.setDescription("bonus");
        when(creditService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new CreditSearchResult("cur-4", true, List.of(model))));

        StepVerifier.create(resolver.credits(sampleFilter(), 0, 100, "cur-4"))
                .assertNext(result -> {
                    assertEquals("cur-4", result.cursor());
                    assertTrue(result.hasNextPage());
                    assertEquals(1, result.result().size());
                    Credit first = result.result().get(0);
                    assertEquals("42", first.id());
                    assertEquals(7L, first.clientId());
                    assertEquals("bonus", first.description());
                })
                .verifyComplete();

        ArgumentCaptor<CreditFilter> filterCaptor = ArgumentCaptor.forClass(CreditFilter.class);
        verify(creditService).search(filterCaptor.capture(), eq("cur-4"), eq(0), eq(100));
        CreditFilter filter = filterCaptor.getValue();
        assertNotNull(filter.getClient());
        assertEquals("exact-eq", filter.getClient().getName().getFirstName().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D10), filter.getDate().getUtc().getEq());
        assertEquals(1, filter.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), filter.getAnd().getFirst().getDate().getUtc().getEq());
        assertEquals(1, filter.getOr().size());
        assertNotNull(filter.getNot());
    }

    @Test
    void credits_passesNullArgumentsThrough() {
        when(creditService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new CreditSearchResult(null, false, List.of())));

        StepVerifier.create(resolver.credits(null, null, null, null))
                .assertNext(result -> {
                    assertNull(result.cursor());
                    assertFalse(result.hasNextPage());
                    assertEquals(0, result.result().size());
                })
                .verifyComplete();

        verify(creditService).search(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void credits_propagatesServiceError() {
        when(creditService.search(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("credit boom")));

        StepVerifier.create(resolver.credits(sampleFilter(), 0, 10, null))
                .expectErrorSatisfies(e -> assertEquals("credit boom", e.getMessage()))
                .verify();
    }

    @Test
    void credits_emptyServiceResultCompletesEmpty() {
        when(creditService.search(any(), any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(resolver.credits(sampleFilter(), 0, 10, null))
                .verifyComplete();
    }

    @Test
    void getClientForCredit_loadsClientByClientId() {
        Client client = new Client(7L, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
        when(clientDataLoader.load(7L)).thenReturn(CompletableFuture.completedFuture(client));

        Credit credit = new Credit("42", 7L, null, null, null, null);

        StepVerifier.create(resolver.getClientForCredit(credit, clientDataLoader))
                .expectNext(client)
                .verifyComplete();

        verify(clientDataLoader).load(7L);
    }

    @Test
    void getClientForCredit_nullResultCompletesEmpty() {
        when(clientDataLoader.load(7L)).thenReturn(CompletableFuture.completedFuture(null));

        Credit credit = new Credit("42", 7L, null, null, null, null);

        StepVerifier.create(resolver.getClientForCredit(credit, clientDataLoader))
                .verifyComplete();
    }
}
