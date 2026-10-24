package com.grunvald.sorm.service.whmcsdata.dataservice.contact;

import com.grunvald.sorm.service.whmcsdata.dataservice.contact.dto.ContactSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact;
import com.grunvald.sorm.service.whmcsdata.filter.ContactFilter;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {

    @Mock
    private ContactRepository repository;

    @Captor
    private ArgumentCaptor<List<Contact>> listCaptor;
    @Captor
    private ArgumentCaptor<Set<Long>> idSetCaptor;

    private ContactServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ContactServiceImpl(repository);
    }

    private Contact contact(long id, String email) {
        var c = new Contact();
        c.setId(id);
        c.setEmail(email);
        return c;
    }

    @Test
    void getAllActiveIds_collectsFromRepository() {
        when(repository.getAllIds()).thenReturn(Flux.just(1L, 2L));

        StepVerifier.create(service.getAllActiveIds())
                .expectNext(List.of(1L, 2L))
                .verifyComplete();
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
        var contacts = List.of(contact(1, "a@b.c"), contact(2, "d@e.f"));
        when(repository.findAllByIdIn(Set.of(1L, 2L))).thenReturn(Flux.fromIterable(contacts));

        StepVerifier.create(service.getAllByIds(Set.of(1L, 2L)))
                .expectNext(contacts)
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
    void saveOrUpdateAll_newContact_added() {
        when(repository.findAllByIdIn(Set.of(1L))).thenReturn(Flux.empty());
        when(repository.addAll(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAll(List.of(contact(1, "a@b.c"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAll(listCaptor.capture());
        assertEquals(1, listCaptor.getValue().size());
        verify(repository, never()).updateAll(anyList());
    }

    @Test
    void saveOrUpdateAll_changedContact_updatesLocalInstance() {
        var existing = contact(1, "old@b.c");
        var incoming = contact(1, "new@b.c");
        when(repository.findAllByIdIn(Set.of(1L))).thenReturn(Flux.just(existing));
        when(repository.updateAll(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAll(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAll(anyList());
        verify(repository).updateAll(listCaptor.capture());
        assertEquals("new@b.c", listCaptor.getValue().getFirst().getEmail());
    }

    @Test
    void saveOrUpdateAll_unchangedContact_nothingSaved() {
        when(repository.findAllByIdIn(Set.of(1L))).thenReturn(Flux.just(contact(1, "same@b.c")));

        StepVerifier.create(service.saveOrUpdateAll(List.of(contact(1, "same@b.c"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAll(anyList());
        verify(repository, never()).updateAll(anyList());
    }

    @Test
    void search_delegatesToRepository() {
        var filter = new ContactFilter();
        var result = new ContactSearchResult("c", false, List.of());
        when(repository.findAllByFilter(filter, "c", 0, 25)).thenReturn(Mono.just(result));

        StepVerifier.create(service.search(filter, "c", 0, 25))
                .expectNext(result)
                .verifyComplete();
    }

    @Test
    void deleteAll_emptySet_returnsTrueWithoutRepositoryCalls() {
        StepVerifier.create(service.deleteAll(Set.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).softDeleteAll(anySet());
    }

    @Test
    void deleteAll_delegatesToRepository() {
        when(repository.softDeleteAll(Set.of(1L, 2L))).thenReturn(Mono.just(true));

        StepVerifier.create(service.deleteAll(Set.of(1L, 2L)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).softDeleteAll(idSetCaptor.capture());
        assertEquals(Set.of(1L, 2L), idSetCaptor.getValue());
    }
}
