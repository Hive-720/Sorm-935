package com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod;

import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.dto.PaymentMethodSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;
import com.grunvald.sorm.service.whmcsdata.filter.PaymentMethodFilter;
import com.grunvald.sorm.util.DateUtils;
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

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceImplTest {

    @Mock
    private PaymentMethodRepository repository;
    @Mock
    private DateUtils date;

    @Captor
    private ArgumentCaptor<List<PaymentMethod>> listCaptor;

    private PaymentMethodServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PaymentMethodServiceImpl(repository, date);
    }

    private PaymentMethod paymentMethod(Long id, String module, String displayName, boolean deleted) {
        var pm = new PaymentMethod();
        pm.setId(id);
        pm.setModule(module);
        pm.setDisplayName(displayName);
        pm.setDeleted(deleted);
        return pm;
    }

    @Test
    void search_delegatesToRepository() {
        var filter = new PaymentMethodFilter();
        var result = new PaymentMethodSearchResult("cursor", false, List.of());
        when(repository.findAllByFilter(filter, "c", 0, 25)).thenReturn(Mono.just(result));

        StepVerifier.create(service.search(filter, "c", 0, 25))
                .expectNext(result)
                .verifyComplete();
    }

    @Test
    void saveOrUpdateAll_emptyList_returnsTrueWithoutRepositoryCalls() {
        StepVerifier.create(service.saveOrUpdateAll(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).findAll();
    }

    @Test
    void saveOrUpdateAll_newModule_addedWithTimestamp() {
        var now = OffsetDateTime.parse("2026-01-02T03:04:05Z");
        var incoming = paymentMethod(1L, "stripe", "Stripe", false);
        when(date.now()).thenReturn(now);
        when(repository.findAll()).thenReturn(Flux.empty());
        when(repository.addAll(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAll(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAll(listCaptor.capture());
        var added = listCaptor.getValue();
        assertEquals(1, added.size());
        assertEquals("stripe", added.get(0).getModule());
        assertEquals(now, added.get(0).getUpdatedAt(), "новая запись должна получить updatedAt от date.now()");
        verify(repository, never()).updateAll(anyList());
    }

    @Test
    void saveOrUpdateAll_unchangedExisting_nothingUpdated() {
        var existing = paymentMethod(10L, "stripe", "Stripe", false);
        var incoming = paymentMethod(null, "stripe", "Stripe", false);
        when(repository.findAll()).thenReturn(Flux.just(existing));

        StepVerifier.create(service.saveOrUpdateAll(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAll(anyList());
        verify(repository, never()).updateAll(anyList());
    }

    @Test
    void saveOrUpdateAll_changedExisting_updatesLocalInstance() {
        var now = OffsetDateTime.parse("2026-01-02T03:04:05Z");
        var existing = paymentMethod(10L, "stripe", "Old Name", false);
        var incoming = paymentMethod(null, "stripe", "New Name", false);
        when(date.now()).thenReturn(now);
        when(repository.findAll()).thenReturn(Flux.just(existing));
        when(repository.updateAll(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAll(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).updateAll(listCaptor.capture());
        var updated = listCaptor.getValue();
        assertEquals(1, updated.size());
        assertEquals("New Name", updated.get(0).getDisplayName());
        assertEquals(10L, updated.get(0).getId());
        assertEquals(now, updated.get(0).getUpdatedAt(), "изменённая запись должна получить свежий updatedAt");
    }

    @Test
    void saveOrUpdateAll_missingModule_markedDeleted() {
        var existing = paymentMethod(10L, "obsolete", "Old", false);
        var incoming = paymentMethod(null, "stripe", "Stripe", false);
        when(repository.findAll()).thenReturn(Flux.just(existing));
        when(repository.addAll(anyList())).thenReturn(Mono.just(true));
        when(repository.updateAll(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAll(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).updateAll(listCaptor.capture());
        var updated = listCaptor.getValue();
        assertEquals(1, updated.size());
        assertEquals("obsolete", updated.get(0).getModule());
        assertTrue(updated.get(0).isDeleted());
    }
}
