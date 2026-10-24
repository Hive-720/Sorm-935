package com.grunvald.sorm.service.whmcsdata.dataservice.user;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.dto.UserSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.model.User;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.model.UserClientRelation;
import com.grunvald.sorm.service.whmcsdata.filter.UserFilter;
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
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;
    @Mock
    private ClientService clientService;

    @Captor
    private ArgumentCaptor<List<User>> userListCaptor;
    @Captor
    private ArgumentCaptor<List<UserClientRelation>> relationListCaptor;

    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(repository, clientService);
    }

    private User user(long id, String firstName, String lastName) {
        var user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setFullName(firstName + " " + lastName);
        user.setEmail(firstName.toLowerCase() + "@example.com");
        return user;
    }

    // ---------- getAllByIds ----------

    @Test
    void getAllByIds_emptyIds_returnsEmptyWithoutRepositoryCall() {
        StepVerifier.create(service.getAllByIds(Set.of()))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();

        verify(repository, never()).findAllByIdIn(anySet());
    }

    @Test
    void getAllByIds_returnsAllUsers() {
        var user = user(1, "John", "Doe");
        when(repository.findAllByIdIn(Set.of(1L))).thenReturn(Flux.just(user));

        StepVerifier.create(service.getAllByIds(Set.of(1L)))
                .expectNext(List.of(user))
                .verifyComplete();
    }

    // ---------- getGroupingClientsByUser ----------

    @Test
    void getGroupingClientsByUser_emptyIds_returnsEmptyMap() {
        StepVerifier.create(service.getGroupingClientsByUser(Set.of()))
                .expectNextMatches(Map::isEmpty)
                .verifyComplete();

        verify(repository, never()).findAllRelationsByUserIdIn(anySet());
    }

    @Test
    void getGroupingClientsByUser_noRelations_returnsEmptyMap() {
        when(repository.findAllRelationsByUserIdIn(Set.of(1L))).thenReturn(Flux.empty());

        StepVerifier.create(service.getGroupingClientsByUser(Set.of(1L)))
                .expectNextMatches(Map::isEmpty)
                .verifyComplete();

        verify(clientService, never()).getAllClientByIds(anySet());
    }

    @Test
    void getGroupingClientsByUser_groupsClientsByUser() {
        var relation1 = new UserClientRelation(1L, 10L, null);
        var relation2 = new UserClientRelation(1L, 11L, null);
        var relation3 = new UserClientRelation(2L, 11L, null);
        when(repository.findAllRelationsByUserIdIn(Set.of(1L, 2L)))
                .thenReturn(Flux.just(relation1, relation2, relation3));

        var client10 = new Client();
        client10.setId(10L);
        var client11 = new Client();
        client11.setId(11L);
        when(clientService.getAllClientByIds(Set.of(10L, 11L)))
                .thenReturn(Mono.just(List.of(client10, client11)));

        StepVerifier.create(service.getGroupingClientsByUser(Set.of(1L, 2L)))
                .assertNext(grouping -> {
                    assertEquals(2, grouping.size());
                    assertEquals(List.of(client10, client11), grouping.get(1L));
                    assertEquals(List.of(client11), grouping.get(2L));
                })
                .verifyComplete();
    }

    @Test
    void getGroupingClientsByUser_skipsUnknownClients() {
        when(repository.findAllRelationsByUserIdIn(Set.of(1L)))
                .thenReturn(Flux.just(new UserClientRelation(1L, 999L, null)));

        var client10 = new Client();
        client10.setId(10L);
        when(clientService.getAllClientByIds(Set.of(999L)))
                .thenReturn(Mono.just(List.of(client10)));

        StepVerifier.create(service.getGroupingClientsByUser(Set.of(1L)))
                .assertNext(grouping -> assertEquals(List.of(), grouping.get(1L)))
                .verifyComplete();
    }

    // ---------- saveOrUpdateAll ----------

    @Test
    void saveOrUpdateAll_emptyList_returnsTrue() {
        StepVerifier.create(service.saveOrUpdateAll(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).findAllByIdIn(anySet());
    }

    @Test
    void saveOrUpdateAll_allNew_addsWithoutUpdate() {
        when(repository.findAllByIdIn(Set.of(1L, 2L))).thenReturn(Flux.empty());
        when(repository.addAll(anyList())).thenReturn(Mono.just(true));

        var user1 = user(1, "John", "Doe");
        var user2 = user(2, "Jane", "Doe");

        StepVerifier.create(service.saveOrUpdateAll(List.of(user1, user2)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAll(List.of(user1, user2));
        verify(repository, never()).updateAll(anyList());
    }

    @Test
    void saveOrUpdateAll_unchanged_skipsPersistence() {
        var user1 = user(1, "John", "Doe");
        when(repository.findAllByIdIn(Set.of(1L))).thenReturn(Flux.just(user1));

        StepVerifier.create(service.saveOrUpdateAll(List.of(user(1, "John", "Doe"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAll(anyList());
        verify(repository, never()).updateAll(anyList());
    }

    @Test
    void saveOrUpdateAll_changed_updatesExisting() {
        var existing = user(1, "John", "Doe");
        when(repository.findAllByIdIn(Set.of(1L))).thenReturn(Flux.just(existing));
        when(repository.updateAll(anyList())).thenReturn(Mono.just(true));

        var incoming = user(1, "John", "Smith");

        StepVerifier.create(service.saveOrUpdateAll(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).updateAll(userListCaptor.capture());
        assertEquals(1, userListCaptor.getValue().size());
        assertEquals("Smith", userListCaptor.getValue().getFirst().getLastName());
        verify(repository, never()).addAll(anyList());
    }

    @Test
    void saveOrUpdateAll_mixed_savesAndUpdates() {
        var existing = user(1, "John", "Doe");
        when(repository.findAllByIdIn(Set.of(1L, 2L))).thenReturn(Flux.just(existing));
        when(repository.addAll(anyList())).thenReturn(Mono.just(true));
        when(repository.updateAll(anyList())).thenReturn(Mono.just(true));

        var changed = user(1, "John", "Smith");
        var fresh = user(2, "Jane", "Doe");

        StepVerifier.create(service.saveOrUpdateAll(List.of(changed, fresh)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAll(List.of(fresh));
        verify(repository).updateAll(List.of(existing));
    }

    // ---------- saveOrRemoveRelationsAll ----------

    @Test
    void saveOrRemoveRelationsAll_emptyList_returnsTrue() {
        StepVerifier.create(service.saveOrRemoveRelationsAll(List.of()))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void saveOrRemoveRelationsAll_addsMissingAndRemovesStale() {
        var existingRelation = new UserClientRelation(1L, 10L, null);
        var staleRelation = new UserClientRelation(1L, 11L, null);
        when(repository.findAllRelationsByUserIdIn(Set.of(1L)))
                .thenReturn(Flux.just(existingRelation, staleRelation));

        var client10 = new Client();
        client10.setId(10L);
        var client12 = new Client();
        client12.setId(12L);
        when(clientService.getAllClientByIds(Set.of(10L, 12L)))
                .thenReturn(Mono.just(List.of(client10, client12)));
        when(repository.addAllRelations(anyList())).thenReturn(Mono.just(true));
        when(repository.deleteAllRelations(anyList())).thenReturn(Mono.just(true));

        var incomingRelation = new UserClientRelation(1L, 12L, null);

        StepVerifier.create(service.saveOrRemoveRelationsAll(List.of(incomingRelation, existingRelation)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAllRelations(relationListCaptor.capture());
        assertEquals(List.of(incomingRelation), relationListCaptor.getValue());
        verify(repository).deleteAllRelations(List.of(staleRelation));
    }

    @Test
    void saveOrRemoveRelationsAll_dropsRelationsWithUnknownClients() {
        when(repository.findAllRelationsByUserIdIn(Set.of(1L)))
                .thenReturn(Flux.just(new UserClientRelation(1L, 10L, null)));

        when(clientService.getAllClientByIds(Set.of(999L))).thenReturn(Mono.just(List.of()));
        when(repository.deleteAllRelations(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrRemoveRelationsAll(
                        List.of(new UserClientRelation(1L, 999L, null))))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAllRelations(anyList());
        verify(repository).deleteAllRelations(anyList());
    }

    @Test
    void saveOrRemoveRelationsAll_noChanges_callsNothing() {
        var existingRelation = new UserClientRelation(1L, 10L, null);
        when(repository.findAllRelationsByUserIdIn(Set.of(1L)))
                .thenReturn(Flux.just(existingRelation));

        var client10 = new Client();
        client10.setId(10L);
        when(clientService.getAllClientByIds(Set.of(10L))).thenReturn(Mono.just(List.of(client10)));

        StepVerifier.create(service.saveOrRemoveRelationsAll(List.of(existingRelation)))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAllRelations(anyList());
        verify(repository, never()).deleteAllRelations(anyList());
    }

    // ---------- search ----------

    @Test
    void search_delegatesToRepository() {
        var filter = new UserFilter();
        var expected = new UserSearchResult(null, false, List.of());
        when(repository.findAllByFilter(filter, "2", 0, 25)).thenReturn(Mono.just(expected));

        StepVerifier.create(service.search(filter, "2", 0, 25))
                .expectNext(expected)
                .verifyComplete();
    }
}
