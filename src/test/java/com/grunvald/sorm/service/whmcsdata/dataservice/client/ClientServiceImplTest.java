package com.grunvald.sorm.service.whmcsdata.dataservice.client;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientGroupSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientIdsResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientLoginSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientProductSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct;
import com.grunvald.sorm.service.whmcsdata.filter.ClientFilter;
import com.grunvald.sorm.service.whmcsdata.filter.ClientGroupFilter;
import com.grunvald.sorm.service.whmcsdata.filter.ClientLoginFilter;
import com.grunvald.sorm.service.whmcsdata.filter.ClientProductFilter;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository repository;
    @Mock
    private DateUtils date;

    @Captor
    private ArgumentCaptor<List<Client>> clientListCaptor;
    @Captor
    private ArgumentCaptor<List<ClientGroup>> groupListCaptor;
    @Captor
    private ArgumentCaptor<List<ClientProduct>> productListCaptor;
    @Captor
    private ArgumentCaptor<Set<Long>> idSetCaptor;

    private ClientServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ClientServiceImpl(date, repository);
    }

    private Client client(long id, String fullName) {
        var c = new Client();
        c.setId(id);
        c.setFullName(fullName);
        return c;
    }

    private ClientGroup group(long id, String name) {
        var g = new ClientGroup(name, null);
        g.setId(id);
        return g;
    }

    private ClientProduct product(long id, long clientId, String name) {
        var p = new ClientProduct();
        p.setId(id);
        p.setClientId(clientId);
        p.setName(name);
        return p;
    }

    @Test
    void getAllClientByIds_emptySet_shortCircuits() {
        StepVerifier.create(service.getAllClientByIds(Set.of()))
                .expectNext(List.of())
                .verifyComplete();

        verify(repository, never()).findAllClientByIdIn(anySet());
    }

    @Test
    void getAllClientByIds_collectsFromRepository() {
        var clients = List.of(client(1, "a"), client(2, "b"));
        when(repository.findAllClientByIdIn(Set.of(1L, 2L))).thenReturn(Flux.fromIterable(clients));

        StepVerifier.create(service.getAllClientByIds(Set.of(1L, 2L)))
                .expectNext(clients)
                .verifyComplete();
    }

    @Test
    void getAllClient_delegatesToRepository() {
        var result = new ClientIdsResult(10L, true, List.of(1L, 2L));
        when(repository.findAll(5L, 25)).thenReturn(Mono.just(result));

        StepVerifier.create(service.getAllClient(5L, 25))
                .expectNext(result)
                .verifyComplete();
    }

    @Test
    void getAllClientGroupByIds_emptySet_shortCircuits() {
        StepVerifier.create(service.getAllClientGroupByIds(Set.of()))
                .expectNext(List.of())
                .verifyComplete();

        verify(repository, never()).findAllClientGroupByIdIn(anySet());
    }

    @Test
    void getAllClientGroupByIds_collectsFromRepository() {
        var groups = List.of(group(1, "g1"), group(2, "g2"));
        when(repository.findAllClientGroupByIdIn(Set.of(1L, 2L))).thenReturn(Flux.fromIterable(groups));

        StepVerifier.create(service.getAllClientGroupByIds(Set.of(1L, 2L)))
                .expectNext(groups)
                .verifyComplete();
    }

    @Test
    void searchClients_delegatesToRepository() {
        var filter = new ClientFilter();
        var result = new ClientSearchResult("c", false, List.of());
        when(repository.findAllClientByFilter(filter, "c", 0, 25)).thenReturn(Mono.just(result));

        StepVerifier.create(service.searchClients(filter, "c", 0, 25))
                .expectNext(result)
                .verifyComplete();
    }

    @Test
    void searchClientProducts_delegatesToRepository() {
        var filter = new ClientProductFilter();
        var result = new ClientProductSearchResult("c", false, List.of());
        when(repository.findAllClientProductByFilter(filter, "c", 0, 25)).thenReturn(Mono.just(result));

        StepVerifier.create(service.searchClientProducts(filter, "c", 0, 25))
                .expectNext(result)
                .verifyComplete();
    }

    @Test
    void searchClientGroups_delegatesToRepository() {
        var filter = new ClientGroupFilter();
        var result = new ClientGroupSearchResult("c", false, List.of());
        when(repository.findAllClientGroupByFilter(filter, "c", 0, 25)).thenReturn(Mono.just(result));

        StepVerifier.create(service.searchClientGroups(filter, "c", 0, 25))
                .expectNext(result)
                .verifyComplete();
    }

    @Test
    void saveOrUpdateAllClientGroup_emptyList_returnsTrueWithoutRepositoryCalls() {
        StepVerifier.create(service.saveOrUpdateAllClientGroup(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).findAllClientGroup();
    }

    @Test
    void saveOrUpdateAllClientGroup_newGroup_addedWithTimestamp() {
        var now = OffsetDateTime.parse("2026-01-02T03:04:05Z");
        var incoming = group(1, "new");
        when(date.now()).thenReturn(now);
        when(repository.findAllClientGroup()).thenReturn(Flux.empty());
        when(repository.addAllClientGroup(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAllClientGroup(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAllClientGroup(groupListCaptor.capture());
        assertEquals(1, groupListCaptor.getValue().size());
        assertEquals(now, groupListCaptor.getValue().getFirst().getUpdatedAt());
        verify(repository, never()).updateAllClientGroup(anyList());
    }

    @Test
    void saveOrUpdateAllClientGroup_changedGroup_updatesLocalInstance() {
        var now = OffsetDateTime.parse("2026-01-02T03:04:05Z");
        var existing = group(1, "old");
        var incoming = group(1, "renamed");
        when(date.now()).thenReturn(now);
        when(repository.findAllClientGroup()).thenReturn(Flux.just(existing));
        when(repository.updateAllClientGroup(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAllClientGroup(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAllClientGroup(anyList());
        verify(repository).updateAllClientGroup(groupListCaptor.capture());
        var updated = groupListCaptor.getValue();
        assertEquals(1, updated.size());
        assertEquals("renamed", updated.get(0).getName());
        assertEquals(now, updated.get(0).getUpdatedAt());
    }

    @Test
    void saveOrUpdateAllClientGroup_unchangedGroup_nothingSaved() {
        var existing = group(1, "same");
        when(repository.findAllClientGroup()).thenReturn(Flux.just(existing));

        StepVerifier.create(service.saveOrUpdateAllClientGroup(List.of(group(1, "same"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAllClientGroup(anyList());
        verify(repository, never()).updateAllClientGroup(anyList());
    }

    @Test
    void saveOrUpdateAllClientGroup_groupsAbsentInIncoming_markedDeleted() {
        var stale = group(99, "stale");
        var incoming = group(1, "new");
        when(date.now()).thenReturn(OffsetDateTime.parse("2026-01-02T03:04:05Z"));
        when(repository.findAllClientGroup()).thenReturn(Flux.just(stale));
        when(repository.addAllClientGroup(anyList())).thenReturn(Mono.just(true));
        when(repository.updateAllClientGroup(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAllClientGroup(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).updateAllClientGroup(groupListCaptor.capture());
        var updated = groupListCaptor.getValue();
        assertEquals(1, updated.size());
        assertEquals(99L, updated.get(0).getId());
        assertTrue(updated.get(0).isDeleted(), "отсутствующая во входящих данных группа должна быть soft-deleted");
    }

    @Test
    void saveAllClient_emptyList_returnsTrueWithoutRepositoryCalls() {
        StepVerifier.create(service.saveAllClient(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).findAllClientByIdIn(anySet());
    }

    @Test
    void saveAllClient_allNew_added() {
        var incoming = List.of(client(1, "a"), client(2, "b"));
        when(repository.findAllClientByIdIn(Set.of(1L, 2L))).thenReturn(Flux.empty());
        when(repository.addAllClient(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveAllClient(incoming))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAllClient(clientListCaptor.capture());
        assertEquals(2, clientListCaptor.getValue().size());
    }

    @Test
    void saveAllClient_allAlreadyExist_nothingAdded() {
        var incoming = List.of(client(1, "a"));
        when(repository.findAllClientByIdIn(Set.of(1L))).thenReturn(Flux.just(client(1, "a")));

        StepVerifier.create(service.saveAllClient(incoming))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAllClient(anyList());
    }

    @Test
    void saveAllClient_mixed_addsOnlyMissing() {
        var incoming = List.of(client(1, "existing"), client(2, "missing"));
        when(repository.findAllClientByIdIn(Set.of(1L, 2L))).thenReturn(Flux.just(client(1, "existing")));
        when(repository.addAllClient(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveAllClient(incoming))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAllClient(clientListCaptor.capture());
        assertEquals(1, clientListCaptor.getValue().size());
        assertEquals(2L, clientListCaptor.getValue().get(0).getId());
    }

    @Test
    void updateAllClient_emptyList_returnsTrueWithoutRepositoryCalls() {
        StepVerifier.create(service.updateAllClient(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).findAllClientByIdIn(anySet());
    }

    @Test
    void updateAllClient_changed_updatesLocalInstance() {
        var existing = client(1, "old");
        var incoming = client(1, "new");
        when(repository.findAllClientByIdIn(Set.of(1L))).thenReturn(Flux.just(existing));
        when(repository.updateAllClient(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.updateAllClient(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).updateAllClient(clientListCaptor.capture());
        assertEquals(1, clientListCaptor.getValue().size());
        assertEquals("new", clientListCaptor.getValue().getFirst().getFullName());
    }

    @Test
    void updateAllClient_unchanged_nothingUpdated() {
        when(repository.findAllClientByIdIn(Set.of(1L))).thenReturn(Flux.just(client(1, "same")));

        StepVerifier.create(service.updateAllClient(List.of(client(1, "same"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).updateAllClient(anyList());
    }

    @Test
    void saveOrUpdateAllClientProducts_emptyList_returnsTrueWithoutRepositoryCalls() {
        StepVerifier.create(service.saveOrUpdateAllClientProducts(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).findAllClientProductByIdIn(anySet());
    }

    @Test
    void saveOrUpdateAllClientProducts_newProduct_added() {
        var incoming = product(1, 10, "p1");
        when(repository.findAllClientProductByIdIn(Set.of(1L))).thenReturn(Flux.empty());
        when(repository.addAllClientProduct(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAllClientProducts(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAllClientProduct(productListCaptor.capture());
        assertEquals(1, productListCaptor.getValue().size());
        verify(repository, never()).updateAllClientProduct(anyList());
    }

    @Test
    void saveOrUpdateAllClientProducts_changedProduct_updatesLocalInstance() {
        var existing = product(1, 10, "old");
        var incoming = product(1, 10, "new");
        when(repository.findAllClientProductByIdIn(Set.of(1L))).thenReturn(Flux.just(existing));
        when(repository.updateAllClientProduct(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAllClientProducts(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAllClientProduct(anyList());
        verify(repository).updateAllClientProduct(productListCaptor.capture());
        assertEquals("new", productListCaptor.getValue().getFirst().getName());
    }

    @Test
    void saveOrUpdateAllClientProducts_unchangedProduct_nothingSaved() {
        when(repository.findAllClientProductByIdIn(Set.of(1L))).thenReturn(Flux.just(product(1, 10, "same")));

        StepVerifier.create(service.saveOrUpdateAllClientProducts(List.of(product(1, 10, "same"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAllClientProduct(anyList());
        verify(repository, never()).updateAllClientProduct(anyList());
    }

    @Test
    void saveAllClientLogin_emptyList_returnsTrueWithoutRepositoryCalls() {
        StepVerifier.create(service.saveAllClientLogin(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAllClientLogin(anyList());
    }

    @Test
    void saveAllClientLogin_delegatesToRepository() {
        var logins = List.of(new ClientLogin(1L, "127.0.0.1", OffsetDateTime.parse("2026-01-01T00:00:00Z")));
        when(repository.addAllClientLogin(logins)).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveAllClientLogin(logins))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void removeAllClient_emptySet_returnsTrueWithoutRepositoryCalls() {
        StepVerifier.create(service.removeAllClient(Set.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).softDeleteAllClient(anySet());
    }

    @Test
    void removeAllClient_delegatesToRepository() {
        when(repository.softDeleteAllClient(Set.of(1L, 2L))).thenReturn(Mono.just(true));

        StepVerifier.create(service.removeAllClient(Set.of(1L, 2L)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).softDeleteAllClient(idSetCaptor.capture());
        assertEquals(Set.of(1L, 2L), idSetCaptor.getValue());
    }

    @Test
    void getLatestLastLoginAllByClientId_emptySet_shortCircuits() {
        StepVerifier.create(service.getLatestLastLoginAllByClientId(Set.of()))
                .expectNext(List.of())
                .verifyComplete();

        verify(repository, never()).findAllClientLastLoginByClientIdIn(anySet());
    }

    @Test
    void getLatestLastLoginAllByClientId_collectsFromRepository() {
        var logins = List.of(new ClientLogin(1L, "127.0.0.1", OffsetDateTime.parse("2026-01-01T00:00:00Z")));
        when(repository.findAllClientLastLoginByClientIdIn(Set.of(1L))).thenReturn(Flux.fromIterable(logins));

        StepVerifier.create(service.getLatestLastLoginAllByClientId(Set.of(1L)))
                .expectNext(logins)
                .verifyComplete();
    }

    @Test
    void searchClientLogins_delegatesToRepository() {
        var filter = new ClientLoginFilter();
        var result = new ClientLoginSearchResult("c", false, List.of());
        when(repository.findAllClientLoginByFilter(filter, "c", 0, 25)).thenReturn(Mono.just(result));

        StepVerifier.create(service.searchClientLogins(filter, "c", 0, 25))
                .expectNext(result)
                .verifyComplete();
    }
}
