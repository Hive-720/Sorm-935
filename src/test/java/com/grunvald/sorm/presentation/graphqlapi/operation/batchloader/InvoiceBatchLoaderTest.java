package com.grunvald.sorm.presentation.graphqlapi.operation.batchloader;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Invoice;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.InvoicePresentationMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.InvoiceService;
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
class InvoiceBatchLoaderTest {

    @Mock
    private InvoiceService service;

    @Mock
    private InvoicePresentationMapper mapper;

    private InvoiceBatchLoader loader;

    @BeforeEach
    void setUp() {
        loader = new InvoiceBatchLoader(service, mapper);
    }

    private static com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice model(Long id) {
        var m = new com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice();
        m.setId(id);
        return m;
    }

    private static Invoice presentation(Long id) {
        return new Invoice(id, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
    }

    @Test
    void load_batchesRequestedIdsInSingleServiceCallAndReturnsMapKeyedById() {
        var ids = Set.of(101L, 202L);
        var model101 = model(101L);
        var model202 = model(202L);
        when(service.getAllByIds(ids)).thenReturn(Mono.just(List.of(model101, model202)));
        when(mapper.map(model101)).thenReturn(presentation(101L));
        when(mapper.map(model202)).thenReturn(presentation(202L));

        Map<Long, Invoice> result = loader.load(ids).toCompletableFuture().join();

        assertEquals(Map.of(101L, presentation(101L), 202L, presentation(202L)), result);
        verify(service, times(1)).getAllByIds(ids);
        verify(mapper).map(model101);
        verify(mapper).map(model202);
    }

    @Test
    void load_emptyIds_returnsEmptyMapAndStillDelegatesToService() {
        when(service.getAllByIds(Set.of())).thenReturn(Mono.just(List.of()));

        Map<Long, Invoice> result = loader.load(Set.of()).toCompletableFuture().join();

        assertTrue(result.isEmpty());
        verify(service).getAllByIds(Set.of());
        verifyNoInteractions(mapper);
    }

    @Test
    void load_serviceReturnsEmptyList_returnsEmptyMapWithoutMapping() {
        when(service.getAllByIds(Set.of(1L, 2L))).thenReturn(Mono.just(List.of()));

        Map<Long, Invoice> result = loader.load(Set.of(1L, 2L)).toCompletableFuture().join();

        assertTrue(result.isEmpty());
        verifyNoInteractions(mapper);
    }

    @Test
    void load_serviceError_propagatesAsFailedFuture() {
        var boom = new IllegalStateException("db down");
        when(service.getAllByIds(Set.of(1L))).thenReturn(Mono.error(boom));

        CompletionException thrown = assertThrows(CompletionException.class,
                () -> loader.load(Set.of(1L)).toCompletableFuture().join());

        assertSame(boom, thrown.getCause());
        verifyNoInteractions(mapper);
    }
}
