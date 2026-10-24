package com.grunvald.sorm.presentation.graphqlapi.operation.batchloader;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientPresentationMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientsByUserBatchLoaderTest {

    @Mock
    private UserService userService;

    @Mock
    private ClientPresentationMapper mapper;

    private ClientsByUserBatchLoader loader;

    @BeforeEach
    void setUp() {
        loader = new ClientsByUserBatchLoader(userService, mapper);
    }

    private static com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client model(Long id) {
        var m = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client();
        m.setId(id);
        return m;
    }

    private static Client presentation(Long id) {
        return new Client(id, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
    }

    @Test
    void load_mapsEveryClientAndPreservesGroupingByUser() {
        var userIds = Set.of(10L, 20L);
        var client1 = model(1L);
        var client2 = model(2L);
        var client3 = model(3L);

        Map<Long, List<com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client>> grouped = new LinkedHashMap<>();
        grouped.put(10L, List.of(client1, client2));
        grouped.put(20L, List.of(client3));

        when(userService.getGroupingClientsByUser(userIds)).thenReturn(Mono.just(grouped));
        when(mapper.map(client1)).thenReturn(presentation(1L));
        when(mapper.map(client2)).thenReturn(presentation(2L));
        when(mapper.map(client3)).thenReturn(presentation(3L));

        Map<Long, List<Client>> result = loader.load(userIds).toCompletableFuture().join();

        assertEquals(Set.of(10L, 20L), result.keySet());
        assertEquals(List.of(presentation(1L), presentation(2L)), result.get(10L));
        assertEquals(List.of(presentation(3L)), result.get(20L));
        verify(userService, times(1)).getGroupingClientsByUser(userIds);
        verify(mapper).map(client1);
        verify(mapper).map(client2);
        verify(mapper).map(client3);
    }

    @Test
    void load_userWithoutClients_isAbsentFromResult() {
        var userIds = Set.of(10L, 20L);
        var client1 = model(1L);
        Map<Long, List<com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client>> grouped = Map.of(10L, List.of(client1));
        when(userService.getGroupingClientsByUser(userIds)).thenReturn(Mono.just(grouped));
        when(mapper.map(client1)).thenReturn(presentation(1L));

        Map<Long, List<Client>> result = loader.load(userIds).toCompletableFuture().join();

        assertEquals(Map.of(10L, List.of(presentation(1L))), result);
    }

    @Test
    void load_emptyIds_returnsEmptyMapAndStillDelegatesToService() {
        when(userService.getGroupingClientsByUser(Set.of())).thenReturn(Mono.just(Collections.emptyMap()));

        Map<Long, List<Client>> result = loader.load(Set.of()).toCompletableFuture().join();

        assertTrue(result.isEmpty());
        verify(userService).getGroupingClientsByUser(Set.of());
        verifyNoInteractions(mapper);
    }

    @Test
    void load_serviceReturnsEmptyMap_returnsEmptyMapWithoutMapping() {
        when(userService.getGroupingClientsByUser(Set.of(1L, 2L))).thenReturn(Mono.just(Collections.emptyMap()));

        Map<Long, List<Client>> result = loader.load(Set.of(1L, 2L)).toCompletableFuture().join();

        assertTrue(result.isEmpty());
        verifyNoInteractions(mapper);
    }

    @Test
    void load_serviceError_propagatesAsFailedFuture() {
        var boom = new IllegalStateException("db down");
        when(userService.getGroupingClientsByUser(Set.of(1L))).thenReturn(Mono.error(boom));

        CompletionException thrown = assertThrows(CompletionException.class,
                () -> loader.load(Set.of(1L)).toCompletableFuture().join());

        assertSame(boom, thrown.getCause());
        verifyNoInteractions(mapper);
    }
}
