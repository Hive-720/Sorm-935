package com.grunvald.sorm.service.whmcsdata.dataservice.affiliate;

import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.dto.AffiliateSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;
import com.grunvald.sorm.service.whmcsdata.filter.AffiliateFilter;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AffiliateServiceImplTest {

    @Mock
    private AffiliateRepository repository;

    @Captor
    private ArgumentCaptor<List<Affiliate>> listCaptor;

    private AffiliateServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AffiliateServiceImpl(repository);
    }

    private Affiliate affiliate(long id) {
        var a = new Affiliate();
        a.setId(id);
        return a;
    }

    @Test
    void search_delegatesToRepository() {
        var filter = new AffiliateFilter();
        var result = new AffiliateSearchResult("cursor", false, List.of());
        when(repository.findAllAffiliateByFilter(filter, "c", 0, 25)).thenReturn(Mono.just(result));

        StepVerifier.create(service.search(filter, "c", 0, 25))
                .expectNext(result)
                .verifyComplete();
    }

    @Test
    void getAllByIds_emptySet_shortCircuits() {
        StepVerifier.create(service.getAllByIds(Set.of()))
                .expectNext(List.of())
                .verifyComplete();

        verify(repository, never()).findAllByIdIn(any());
    }

    @Test
    void getAllByIds_collectsFromRepository() {
        var affiliates = List.of(affiliate(1), affiliate(2));
        when(repository.findAllByIdIn(Set.of(1L, 2L))).thenReturn(Flux.fromIterable(affiliates));

        StepVerifier.create(service.getAllByIds(Set.of(1L, 2L)))
                .expectNext(affiliates)
                .verifyComplete();
    }

    @Test
    void saveOrUpdateAll_emptyList_returnsTrueWithoutRepositoryCalls() {
        StepVerifier.create(service.saveOrUpdateAll(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).findAllByIdIn(any());
    }

    @Test
    void saveOrUpdateAll_newIds_added() {
        var incoming = affiliate(1);
        when(repository.findAllByIdIn(Set.of(1L))).thenReturn(Flux.empty());
        when(repository.addAll(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAll(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAll(listCaptor.capture());
        assertEquals(1, listCaptor.getValue().size());
        verify(repository, never()).updateAll(anyList());
    }

    @Test
    void saveOrUpdateAll_changedVisitors_updatesLocalInstance() {
        var existing = affiliate(1);
        existing.setVisitors(100L);

        var incoming = affiliate(1);
        incoming.setVisitors(200L);

        when(repository.findAllByIdIn(Set.of(1L))).thenReturn(Flux.just(existing));
        when(repository.updateAll(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAll(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).updateAll(listCaptor.capture());
        assertEquals(200L, listCaptor.getValue().get(0).getVisitors());
    }

    @Test
    void saveOrUpdateAll_unchangedId_nothingSaved() {
        var existing = affiliate(1);
        existing.setVisitors(100L);

        var incoming = affiliate(1);
        incoming.setVisitors(100L);

        when(repository.findAllByIdIn(Set.of(1L))).thenReturn(Flux.just(existing));

        StepVerifier.create(service.saveOrUpdateAll(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAll(anyList());
        verify(repository, never()).updateAll(anyList());
    }
}
