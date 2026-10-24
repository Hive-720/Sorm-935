package com.grunvald.sorm.service.whmcsdata.dataservice.invoice;

import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.dto.InvoiceSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice;
import com.grunvald.sorm.service.whmcsdata.filter.InvoiceFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository repository;

    @Captor
    private ArgumentCaptor<List<Invoice>> listCaptor;

    private InvoiceServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new InvoiceServiceImpl(repository);
    }

    private Invoice invoice(long id, String status) {
        var i = new Invoice();
        i.setId(id);
        i.setStatus(status);
        return i;
    }

    @Test
    void getAllByIds_emptySet_shortCircuits() {
        StepVerifier.create(service.getAllByIds(Set.of()))
                .expectNext(List.of())
                .verifyComplete();

        verify(repository, never()).findAllByIdIn(anySet());
    }

    @Test
    void getAllByIds_collectsFromRepository() {
        var invoices = List.of(invoice(1, "Paid"), invoice(2, "Unpaid"));
        when(repository.findAllByIdIn(Set.of(1L, 2L))).thenReturn(Flux.fromIterable(invoices));

        StepVerifier.create(service.getAllByIds(Set.of(1L, 2L)))
                .expectNext(invoices)
                .verifyComplete();
    }

    @Test
    void saveOrUpdateAll_emptyList_returnsTrueWithoutRepositoryCalls() {
        StepVerifier.create(service.saveOrUpdateAll(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).findAllByIdIn(anySet());
    }

    @Test
    void saveOrUpdateAll_newInvoice_added() {
        when(repository.findAllByIdIn(Set.of(1L))).thenReturn(Flux.empty());
        when(repository.addAll(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAll(List.of(invoice(1, "Unpaid"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAll(listCaptor.capture());
        assertEquals(1, listCaptor.getValue().size());
        verify(repository, never()).updateAll(anyList());
    }

    @Test
    void saveOrUpdateAll_changedInvoice_updatesLocalInstance() {
        var existing = invoice(1, "Unpaid");
        var incoming = invoice(1, "Paid");
        when(repository.findAllByIdIn(Set.of(1L))).thenReturn(Flux.just(existing));
        when(repository.updateAll(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAll(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAll(anyList());
        verify(repository).updateAll(listCaptor.capture());
        assertEquals("Paid", listCaptor.getValue().get(0).getStatus());
    }

    @Test
    void saveOrUpdateAll_unchangedInvoice_nothingSaved() {
        when(repository.findAllByIdIn(Set.of(1L))).thenReturn(Flux.just(invoice(1, "Unpaid")));

        StepVerifier.create(service.saveOrUpdateAll(List.of(invoice(1, "Unpaid"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAll(anyList());
        verify(repository, never()).updateAll(anyList());
    }

    @Test
    void search_delegatesToRepository() {
        var filter = new InvoiceFilter();
        var result = new InvoiceSearchResult("c", false, List.of());
        when(repository.findAllByFilter(filter, "c", 0, 25)).thenReturn(Mono.just(result));

        StepVerifier.create(service.search(filter, "c", 0, 25))
                .expectNext(result)
                .verifyComplete();
    }
}
