package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.InvoiceFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Invoice;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.InvoiceService;
import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.dto.InvoiceSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.InvoiceFilter;
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
class InvoiceResolverTest {

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private DataLoader<Long, Client> clientDataLoader;

    private final FilterMappers filterMappers = new FilterMappers();

    private InvoiceResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new InvoiceResolver(invoiceService, filterMappers.invoiceMapper,
                MapStructTestFactory.invoiceResultMapper());
    }

    private InvoiceFilterInput sampleFilter() {
        InvoiceFilterInput nestedAnd = new InvoiceFilterInput(null, SampleFilters.dateTime(SampleFilters.D11),
                null, null, null, null, null, null, null, null, null);
        InvoiceFilterInput nestedOr = new InvoiceFilterInput(null, SampleFilters.dateTime(SampleFilters.D12),
                null, null, null, null, null, null, null, null, null);
        InvoiceFilterInput nestedNot = new InvoiceFilterInput(null, SampleFilters.dateTime(SampleFilters.D13),
                null, null, null, null, null, null, null, null, null);
        return new InvoiceFilterInput(
                SampleFilters.client(),
                SampleFilters.dateTime(SampleFilters.D01),
                SampleFilters.dateTime(SampleFilters.D02),
                SampleFilters.dateTime(SampleFilters.D03),
                SampleFilters.dateTime(SampleFilters.D04),
                SampleFilters.dateTime(SampleFilters.D05),
                SampleFilters.dateTime(SampleFilters.D06),
                SampleFilters.dateTime(SampleFilters.D07),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);
    }

    @Test
    void invoices_mapsFilterAndPassesPaginationArgumentsInOrder() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice();
        model.setId(1L);
        model.setClientId(2L);
        when(invoiceService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new InvoiceSearchResult("cur-6", true, List.of(model))));

        StepVerifier.create(resolver.invoices(sampleFilter(), 15, 25, "cur-6"))
                .assertNext(result -> {
                    assertEquals("cur-6", result.cursor());
                    assertTrue(result.hasNextPage());
                    assertEquals(1, result.result().size());
                    Invoice first = result.result().getFirst();
                    assertEquals(1L, first.id());
                    assertEquals(2L, first.clientId());
                })
                .verifyComplete();

        ArgumentCaptor<InvoiceFilter> filterCaptor = ArgumentCaptor.forClass(InvoiceFilter.class);
        verify(invoiceService).search(filterCaptor.capture(), eq("cur-6"), eq(15), eq(25));
        InvoiceFilter filter = filterCaptor.getValue();
        assertNotNull(filter.getClient());
        assertEquals("exact-eq", filter.getClient().getName().getFirstName().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), filter.getDate().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), filter.getDueDate().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D03), filter.getDatePaid().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D04), filter.getDateRefunded().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D05), filter.getDateCancelled().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D06), filter.getCreatedAt().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D07), filter.getUpdatedAt().getUtc().getEq());
        assertEquals(1, filter.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), filter.getAnd().getFirst().getDate().getUtc().getEq());
        assertEquals(1, filter.getOr().size());
        assertNotNull(filter.getNot());
    }

    @Test
    void invoices_passesNullArgumentsThrough() {
        when(invoiceService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new InvoiceSearchResult(null, false, List.of())));

        StepVerifier.create(resolver.invoices(null, null, null, null))
                .assertNext(result -> {
                    assertNull(result.cursor());
                    assertFalse(result.hasNextPage());
                    assertEquals(0, result.result().size());
                })
                .verifyComplete();

        verify(invoiceService).search(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void invoices_propagatesServiceError() {
        when(invoiceService.search(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("invoice boom")));

        StepVerifier.create(resolver.invoices(sampleFilter(), 0, 10, null))
                .expectErrorSatisfies(e -> assertEquals("invoice boom", e.getMessage()))
                .verify();
    }

    @Test
    void invoices_emptyServiceResultCompletesEmpty() {
        when(invoiceService.search(any(), any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(resolver.invoices(sampleFilter(), 0, 10, null))
                .verifyComplete();
    }

    @Test
    void getClientForInvoice_loadsClientByClientId() {
        Client client = new Client(2L, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
        when(clientDataLoader.load(2L)).thenReturn(CompletableFuture.completedFuture(client));

        Invoice invoice = new Invoice(1L, 2L, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getClientForInvoice(invoice, clientDataLoader))
                .expectNext(client)
                .verifyComplete();

        verify(clientDataLoader).load(2L);
    }

    @Test
    void getClientForInvoice_nullResultCompletesEmpty() {
        when(clientDataLoader.load(2L)).thenReturn(CompletableFuture.completedFuture(null));

        Invoice invoice = new Invoice(1L, 2L, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getClientForInvoice(invoice, clientDataLoader))
                .verifyComplete();
    }
}
