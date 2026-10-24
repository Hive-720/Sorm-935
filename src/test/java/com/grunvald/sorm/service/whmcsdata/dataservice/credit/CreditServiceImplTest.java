package com.grunvald.sorm.service.whmcsdata.dataservice.credit;

import com.grunvald.sorm.service.whmcsdata.dataservice.credit.dto.CreditSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit;
import com.grunvald.sorm.service.whmcsdata.filter.CreditFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditServiceImplTest {

    @Mock
    private CreditRepository repository;

    private CreditServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CreditServiceImpl(repository);
    }

    private Credit credit(long id, long clientId) {
        var c = new Credit();
        c.setId(id);
        c.setClientId(clientId);
        return c;
    }

    @Test
    void saveAll_emptyList_returnsTrueWithoutRepositoryCalls() {
        StepVerifier.create(service.saveAll(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAll(anyList());
    }

    @Test
    void saveAll_delegatesToRepository() {
        var credits = List.of(credit(1, 10), credit(2, 10));
        when(repository.addAll(credits)).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveAll(credits))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void getLatestAllByClientId_emptySet_shortCircuits() {
        StepVerifier.create(service.getLatestAllByClientId(Set.of()))
                .expectNext(List.of())
                .verifyComplete();

        verify(repository, never()).findLatestAllByClientId(anySet());
    }

    @Test
    void getLatestAllByClientId_collectsFromRepository() {
        var credits = List.of(credit(1, 10));
        when(repository.findLatestAllByClientId(Set.of(10L))).thenReturn(Flux.fromIterable(credits));

        StepVerifier.create(service.getLatestAllByClientId(Set.of(10L)))
                .expectNext(credits)
                .verifyComplete();
    }

    @Test
    void search_delegatesToRepository() {
        var filter = new CreditFilter();
        var result = new CreditSearchResult("c", false, List.of());
        when(repository.findAllByFilter(filter, "c", 0, 25)).thenReturn(Mono.just(result));

        StepVerifier.create(service.search(filter, "c", 0, 25))
                .expectNext(result)
                .verifyComplete();
    }
}
