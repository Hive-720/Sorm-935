package com.grunvald.sorm.service.whmcsdata.dataservice.email;

import com.grunvald.sorm.service.whmcsdata.dataservice.email.dto.EmailSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email;
import com.grunvald.sorm.service.whmcsdata.filter.EmailFilter;
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
class EmailServiceImplTest {

    @Mock
    private EmailRepository repository;

    private EmailServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EmailServiceImpl(repository);
    }

    private Email email(long id, String subject) {
        var e = new Email();
        e.setId(id);
        e.setSubject(subject);
        return e;
    }

    @Test
    void search_delegatesToRepository() {
        var filter = new EmailFilter();
        var result = new EmailSearchResult("c", false, List.of());
        when(repository.findAllByFilter(filter, "c", 0, 25)).thenReturn(Mono.just(result));

        StepVerifier.create(service.search(filter, "c", 0, 25))
                .expectNext(result)
                .verifyComplete();
    }

    @Test
    void getLatestAllByClientId_emptySet_shortCircuits() {
        StepVerifier.create(service.getLatestAllByClientId(Set.of()))
                .expectNext(List.of())
                .verifyComplete();

        verify(repository, never()).getLatestAllByClientId(anySet());
    }

    @Test
    void getLatestAllByClientId_collectsFromRepository() {
        var emails = List.of(email(1, "s1"));
        when(repository.getLatestAllByClientId(Set.of(10L))).thenReturn(Flux.fromIterable(emails));

        StepVerifier.create(service.getLatestAllByClientId(Set.of(10L)))
                .expectNext(emails)
                .verifyComplete();
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
        var emails = List.of(email(1, "s1"), email(2, "s2"));
        when(repository.addAll(emails)).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveAll(emails))
                .expectNext(true)
                .verifyComplete();
    }
}
