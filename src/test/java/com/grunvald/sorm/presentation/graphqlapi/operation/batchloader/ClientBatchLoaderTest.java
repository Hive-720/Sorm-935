package com.grunvald.sorm.presentation.graphqlapi.operation.batchloader;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientPresentationMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

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
class ClientBatchLoaderTest {

    @Mock
    private ClientService service;

    @Mock
    private ClientPresentationMapper mapper;

    private ClientBatchLoader loader;

    @BeforeEach
    void setUp() {
        loader = new ClientBatchLoader(service, mapper);
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
    void load_batchesRequestedIdsInSingleServiceCallAndReturnsMapKeyedById() {
        var ids = Set.of(11L, 22L, 33L);
        var model11 = model(11L);
        var model33 = model(33L);
        when(service.getAllClientByIds(ids)).thenReturn(Mono.just(List.of(model11, model33)));
        when(mapper.map(model11)).thenReturn(presentation(11L));
        when(mapper.map(model33)).thenReturn(presentation(33L));

        Map<Long, Client> result = loader.load(ids).toCompletableFuture().join();

        assertEquals(Map.of(11L, presentation(11L), 33L, presentation(33L)), result);
        verify(service, times(1)).getAllClientByIds(ids);
        verify(mapper).map(model11);
        verify(mapper).map(model33);
    }

    @Test
    void load_emptyIds_returnsEmptyMapAndStillDelegatesToService() {
        when(service.getAllClientByIds(Set.of())).thenReturn(Mono.just(List.of()));

        Map<Long, Client> result = loader.load(Set.of()).toCompletableFuture().join();

        assertTrue(result.isEmpty());
        verify(service).getAllClientByIds(Set.of());
        verifyNoInteractions(mapper);
    }

    @Test
    void load_serviceReturnsEmptyList_returnsEmptyMapWithoutMapping() {
        when(service.getAllClientByIds(Set.of(1L, 2L))).thenReturn(Mono.just(List.of()));

        Map<Long, Client> result = loader.load(Set.of(1L, 2L)).toCompletableFuture().join();

        assertTrue(result.isEmpty());
        verifyNoInteractions(mapper);
    }

    @Test
    void load_serviceError_propagatesAsFailedFuture() {
        var boom = new IllegalStateException("db down");
        when(service.getAllClientByIds(Set.of(1L))).thenReturn(Mono.error(boom));

        CompletionException thrown = assertThrows(CompletionException.class,
                () -> loader.load(Set.of(1L)).toCompletableFuture().join());

        assertSame(boom, thrown.getCause());
        verifyNoInteractions(mapper);
    }
}
