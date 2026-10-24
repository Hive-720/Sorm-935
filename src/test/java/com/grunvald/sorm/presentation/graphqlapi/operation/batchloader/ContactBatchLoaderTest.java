package com.grunvald.sorm.presentation.graphqlapi.operation.batchloader;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Contact;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ContactPresentationMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.contact.ContactService;
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
class ContactBatchLoaderTest {

    @Mock
    private ContactService service;

    @Mock
    private ContactPresentationMapper mapper;

    private ContactBatchLoader loader;

    @BeforeEach
    void setUp() {
        loader = new ContactBatchLoader(service, mapper);
    }

    private static com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact model(Long id) {
        var m = new com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact();
        m.setId(id);
        return m;
    }

    private static Contact presentation(Long id) {
        return new Contact(id, null, null, null, null, null, null, null, null, null, null);
    }

    @Test
    void load_batchesRequestedIdsInSingleServiceCallAndReturnsMapKeyedById() {
        var ids = Set.of(1L, 2L, 3L);
        var model2 = model(2L);
        var model3 = model(3L);
        when(service.getAllByIds(ids)).thenReturn(Mono.just(List.of(model2, model3)));
        when(mapper.map(model2)).thenReturn(presentation(2L));
        when(mapper.map(model3)).thenReturn(presentation(3L));

        Map<Long, Contact> result = loader.load(ids).toCompletableFuture().join();

        assertEquals(Map.of(2L, presentation(2L), 3L, presentation(3L)), result);
        verify(service, times(1)).getAllByIds(ids);
        verify(mapper).map(model2);
        verify(mapper).map(model3);
    }

    @Test
    void load_emptyIds_returnsEmptyMapAndStillDelegatesToService() {
        when(service.getAllByIds(Set.of())).thenReturn(Mono.just(List.of()));

        Map<Long, Contact> result = loader.load(Set.of()).toCompletableFuture().join();

        assertTrue(result.isEmpty());
        verify(service).getAllByIds(Set.of());
        verifyNoInteractions(mapper);
    }

    @Test
    void load_serviceReturnsEmptyList_returnsEmptyMapWithoutMapping() {
        when(service.getAllByIds(Set.of(1L, 2L))).thenReturn(Mono.just(List.of()));

        Map<Long, Contact> result = loader.load(Set.of(1L, 2L)).toCompletableFuture().join();

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
