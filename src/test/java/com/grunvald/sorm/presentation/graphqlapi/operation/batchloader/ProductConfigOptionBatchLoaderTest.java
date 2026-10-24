package com.grunvald.sorm.presentation.graphqlapi.operation.batchloader;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ProductConfigOption;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ProductConfigOptionPresentationMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.ProductService;
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
class ProductConfigOptionBatchLoaderTest {

    @Mock
    private ProductService service;

    @Mock
    private ProductConfigOptionPresentationMapper mapper;

    private ProductConfigOptionBatchLoader loader;

    @BeforeEach
    void setUp() {
        loader = new ProductConfigOptionBatchLoader(service, mapper);
    }

    private static com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption model(Long productConfigId) {
        var m = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption();
        m.setProductConfigId(productConfigId);
        return m;
    }

    private static ProductConfigOption presentation(Long productConfigId, String name) {
        return new ProductConfigOption(null, productConfigId, name, null);
    }

    @Test
    void load_groupsOptionsByProductConfigIdPreservingOrderAndMapsEachOption() {
        var productConfigIds = Set.of(10L, 20L);
        var option10a = model(10L);
        var option20 = model(20L);
        var option10b = model(10L);
        when(service.getAllProductConfigOptionByProductConfigIds(productConfigIds))
                .thenReturn(Mono.just(List.of(option10a, option20, option10b)));
        when(mapper.map(option10a)).thenReturn(presentation(10L, "a"));
        when(mapper.map(option20)).thenReturn(presentation(20L, "b"));
        when(mapper.map(option10b)).thenReturn(presentation(10L, "c"));

        Map<Long, List<ProductConfigOption>> result = loader.load(productConfigIds).toCompletableFuture().join();

        assertEquals(Set.of(10L, 20L), result.keySet());
        assertEquals(List.of(presentation(10L, "a"), presentation(10L, "c")), result.get(10L));
        assertEquals(List.of(presentation(20L, "b")), result.get(20L));
        verify(service, times(1)).getAllProductConfigOptionByProductConfigIds(productConfigIds);
        verify(mapper).map(option10a);
        verify(mapper).map(option20);
        verify(mapper).map(option10b);
    }

    @Test
    void load_emptyIds_returnsEmptyMapAndStillDelegatesToService() {
        when(service.getAllProductConfigOptionByProductConfigIds(Set.of())).thenReturn(Mono.just(List.of()));

        Map<Long, List<ProductConfigOption>> result = loader.load(Set.of()).toCompletableFuture().join();

        assertTrue(result.isEmpty());
        verify(service).getAllProductConfigOptionByProductConfigIds(Set.of());
        verifyNoInteractions(mapper);
    }

    @Test
    void load_serviceReturnsEmptyList_returnsEmptyMapWithoutMapping() {
        when(service.getAllProductConfigOptionByProductConfigIds(Set.of(1L, 2L))).thenReturn(Mono.just(List.of()));

        Map<Long, List<ProductConfigOption>> result = loader.load(Set.of(1L, 2L)).toCompletableFuture().join();

        assertTrue(result.isEmpty());
        verifyNoInteractions(mapper);
    }

    @Test
    void load_serviceError_propagatesAsFailedFuture() {
        var boom = new IllegalStateException("db down");
        when(service.getAllProductConfigOptionByProductConfigIds(Set.of(1L))).thenReturn(Mono.error(boom));

        CompletionException thrown = assertThrows(CompletionException.class,
                () -> loader.load(Set.of(1L)).toCompletableFuture().join());

        assertSame(boom, thrown.getCause());
        verifyNoInteractions(mapper);
    }
}
