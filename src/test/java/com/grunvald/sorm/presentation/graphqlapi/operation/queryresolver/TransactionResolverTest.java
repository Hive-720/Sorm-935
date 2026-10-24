package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.TransactionFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Invoice;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Transaction;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.TransactionService;
import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.dto.TransactionSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.TransactionFilter;
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
class TransactionResolverTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private DataLoader<Long, Client> clientDataLoader;

    @Mock
    private DataLoader<Long, Invoice> invoiceDataLoader;

    private final FilterMappers filterMappers = new FilterMappers();

    private TransactionResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new TransactionResolver(transactionService, filterMappers.transactionMapper,
                MapStructTestFactory.transactionResultMapper());
    }

    private TransactionFilterInput sampleFilter() {
        TransactionFilterInput nestedAnd = new TransactionFilterInput(null, null,
                SampleFilters.dateTime(SampleFilters.D11), null, null, null);
        TransactionFilterInput nestedOr = new TransactionFilterInput(null, null,
                SampleFilters.dateTime(SampleFilters.D12), null, null, null);
        TransactionFilterInput nestedNot = new TransactionFilterInput(null, null,
                SampleFilters.dateTime(SampleFilters.D13), null, null, null);
        return new TransactionFilterInput(
                SampleFilters.invoice(),
                SampleFilters.client(),
                SampleFilters.dateTime(SampleFilters.D10),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);
    }

    @Test
    void transactions_mapsFilterAndPassesPaginationArgumentsInOrder() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction();
        model.setId(1L);
        model.setInvoiceId(2L);
        model.setClientId(3L);
        when(transactionService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new TransactionSearchResult("cur-14", true, List.of(model))));

        StepVerifier.create(resolver.transactions(sampleFilter(), 8, 88, "cur-14"))
                .assertNext(result -> {
                    assertEquals("cur-14", result.cursor());
                    assertTrue(result.hasNextPage());
                    assertEquals(1, result.result().size());
                    Transaction first = result.result().get(0);
                    assertEquals(1L, first.id());
                    assertEquals(2L, first.invoiceId());
                    assertEquals(3L, first.clientId());
                })
                .verifyComplete();

        ArgumentCaptor<TransactionFilter> filterCaptor = ArgumentCaptor.forClass(TransactionFilter.class);
        verify(transactionService).search(filterCaptor.capture(), eq("cur-14"), eq(8), eq(88));
        TransactionFilter filter = filterCaptor.getValue();
        assertNotNull(filter.getInvoice());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), filter.getInvoice().getDate().getUtc().getEq());
        assertNotNull(filter.getClient());
        assertEquals("exact-eq", filter.getClient().getName().getFirstName().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D10), filter.getDate().getUtc().getEq());
        assertEquals(1, filter.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), filter.getAnd().get(0).getDate().getUtc().getEq());
        assertEquals(1, filter.getOr().size());
        assertNotNull(filter.getNot());
    }

    @Test
    void transactions_passesNullArgumentsThrough() {
        when(transactionService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new TransactionSearchResult(null, false, List.of())));

        StepVerifier.create(resolver.transactions(null, null, null, null))
                .assertNext(result -> {
                    assertNull(result.cursor());
                    assertFalse(result.hasNextPage());
                    assertEquals(0, result.result().size());
                })
                .verifyComplete();

        verify(transactionService).search(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void transactions_propagatesServiceError() {
        when(transactionService.search(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("transaction boom")));

        StepVerifier.create(resolver.transactions(sampleFilter(), 0, 10, null))
                .expectErrorSatisfies(e -> assertEquals("transaction boom", e.getMessage()))
                .verify();
    }

    @Test
    void transactions_emptyServiceResultCompletesEmpty() {
        when(transactionService.search(any(), any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(resolver.transactions(sampleFilter(), 0, 10, null))
                .verifyComplete();
    }

    @Test
    void getClientForTransaction_loadsClientByClientId() {
        Client client = new Client(3L, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
        when(clientDataLoader.load(3L)).thenReturn(CompletableFuture.completedFuture(client));

        Transaction transaction = new Transaction(1L, 2L, 3L, null, null, null, null, null,
                null, null, null, null, null);

        StepVerifier.create(resolver.getClientForTransaction(transaction, clientDataLoader))
                .expectNext(client)
                .verifyComplete();

        verify(clientDataLoader).load(3L);
    }

    @Test
    void getClientForTransaction_nullResultCompletesEmpty() {
        when(clientDataLoader.load(3L)).thenReturn(CompletableFuture.completedFuture(null));

        Transaction transaction = new Transaction(1L, 2L, 3L, null, null, null, null, null,
                null, null, null, null, null);

        StepVerifier.create(resolver.getClientForTransaction(transaction, clientDataLoader))
                .verifyComplete();
    }

    @Test
    void getInvoiceForTransaction_loadsInvoiceByInvoiceId() {
        Invoice invoice = new Invoice(2L, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
        when(invoiceDataLoader.load(2L)).thenReturn(CompletableFuture.completedFuture(invoice));

        Transaction transaction = new Transaction(1L, 2L, 3L, null, null, null, null, null,
                null, null, null, null, null);

        StepVerifier.create(resolver.getInvoiceForTransaction(transaction, invoiceDataLoader))
                .expectNext(invoice)
                .verifyComplete();

        verify(invoiceDataLoader).load(2L);
    }

    @Test
    void getInvoiceForTransaction_nullResultCompletesEmpty() {
        when(invoiceDataLoader.load(2L)).thenReturn(CompletableFuture.completedFuture(null));

        Transaction transaction = new Transaction(1L, 2L, 3L, null, null, null, null, null,
                null, null, null, null, null);

        StepVerifier.create(resolver.getInvoiceForTransaction(transaction, invoiceDataLoader))
                .verifyComplete();
    }
}
