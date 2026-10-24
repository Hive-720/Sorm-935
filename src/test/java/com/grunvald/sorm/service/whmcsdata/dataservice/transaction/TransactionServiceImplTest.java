package com.grunvald.sorm.service.whmcsdata.dataservice.transaction;

import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.dto.TransactionSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction;
import com.grunvald.sorm.service.whmcsdata.filter.TransactionFilter;
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

import java.math.BigDecimal;
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
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository repository;

    @Captor
    private ArgumentCaptor<List<Transaction>> listCaptor;

    private TransactionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TransactionServiceImpl(repository);
    }

    private Transaction transaction(long id, String gateway) {
        var t = new Transaction();
        t.setId(id);
        t.setGateway(gateway);
        return t;
    }

    @Test
    void getAllByIdIn_emptySet_shortCircuits() {
        StepVerifier.create(service.getAllByIdIn(Set.of()))
                .expectNext(List.of())
                .verifyComplete();

        verify(repository, never()).findAllByIdIn(anySet());
    }

    @Test
    void getAllByIdIn_collectsFromRepository() {
        var transactions = List.of(transaction(1, "gw1"), transaction(2, "gw2"));
        when(repository.findAllByIdIn(Set.of(1L, 2L))).thenReturn(Flux.fromIterable(transactions));

        StepVerifier.create(service.getAllByIdIn(Set.of(1L, 2L)))
                .expectNext(transactions)
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
    void saveOrUpdateAll_newTransaction_added() {
        when(repository.findAllByIdIn(Set.of(1L))).thenReturn(Flux.empty());
        when(repository.addAll(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAll(List.of(transaction(1, "gw1"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAll(listCaptor.capture());
        assertEquals(1, listCaptor.getValue().size());
        verify(repository, never()).updateAll(anyList());
    }

    @Test
    void saveOrUpdateAll_changedTransaction_updatesLocalInstance() {
        var existing = transaction(1, "old");
        var incoming = transaction(1, "new");
        when(repository.findAllByIdIn(Set.of(1L))).thenReturn(Flux.just(existing));
        when(repository.updateAll(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAll(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAll(anyList());
        verify(repository).updateAll(listCaptor.capture());
        assertEquals("new", listCaptor.getValue().get(0).getGateway());
    }

    @Test
    void saveOrUpdateAll_unchangedTransaction_nothingSaved() {
        when(repository.findAllByIdIn(Set.of(1L))).thenReturn(Flux.just(transaction(1, "same")));

        StepVerifier.create(service.saveOrUpdateAll(List.of(transaction(1, "same"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAll(anyList());
        verify(repository, never()).updateAll(anyList());
    }

    @Test
    void search_delegatesToRepository() {
        var filter = new TransactionFilter();
        var result = new TransactionSearchResult("c", false, List.of());
        when(repository.findAllByFilter(filter, "c", 0, 25)).thenReturn(Mono.just(result));

        StepVerifier.create(service.search(filter, "c", 0, 25))
                .expectNext(result)
                .verifyComplete();
    }
}
