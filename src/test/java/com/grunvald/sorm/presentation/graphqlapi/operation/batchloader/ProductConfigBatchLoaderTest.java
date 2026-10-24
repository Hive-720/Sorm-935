package com.grunvald.sorm.presentation.graphqlapi.operation.batchloader;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ProductConfig;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ProductConfigPresentationMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.ProductService;
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
class ProductConfigBatchLoaderTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductConfigPresentationMapper mapper;

    private ProductConfigBatchLoader loader;

    @BeforeEach
    void setUp() {
        loader = new ProductConfigBatchLoader(productService, mapper);
    }

    private static com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfig model(Long id) {
        return new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfig(id, "config-" + id, "type");
    }

    private static ProductConfig presentation(Long id) {
        return new ProductConfig(id, null, null);
    }

    @Test
    void load_mapsEveryConfigAndPreservesGroupingByProduct() {
        var productIds = Set.of(10L, 20L);
        var config1 = model(1L);
        var config2 = model(2L);
        var config3 = model(3L);

        Map<Long, List<com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfig>> grouped = new LinkedHashMap<>();
        grouped.put(10L, List.of(config1, config2));
        grouped.put(20L, List.of(config3));

        when(productService.getAllGroupingProductConfigByProductId(productIds)).thenReturn(Mono.just(grouped));
        when(mapper.map(config1)).thenReturn(presentation(1L));
        when(mapper.map(config2)).thenReturn(presentation(2L));
        when(mapper.map(config3)).thenReturn(presentation(3L));

        Map<Long, List<ProductConfig>> result = loader.load(productIds).toCompletableFuture().join();

        assertEquals(Set.of(10L, 20L), result.keySet());
        assertEquals(List.of(presentation(1L), presentation(2L)), result.get(10L));
        assertEquals(List.of(presentation(3L)), result.get(20L));
        verify(productService, times(1)).getAllGroupingProductConfigByProductId(productIds);
        verify(mapper).map(config1);
        verify(mapper).map(config2);
        verify(mapper).map(config3);
    }

    @Test
    void load_productWithoutConfigs_isAbsentFromResult() {
        var productIds = Set.of(10L, 20L);
        var config1 = model(1L);
        Map<Long, List<com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfig>> grouped = Map.of(10L, List.of(config1));
        when(productService.getAllGroupingProductConfigByProductId(productIds)).thenReturn(Mono.just(grouped));
        when(mapper.map(config1)).thenReturn(presentation(1L));

        Map<Long, List<ProductConfig>> result = loader.load(productIds).toCompletableFuture().join();

        assertEquals(Map.of(10L, List.of(presentation(1L))), result);
    }

    @Test
    void load_emptyIds_returnsEmptyMapAndStillDelegatesToService() {
        when(productService.getAllGroupingProductConfigByProductId(Set.of())).thenReturn(Mono.just(Collections.emptyMap()));

        Map<Long, List<ProductConfig>> result = loader.load(Set.of()).toCompletableFuture().join();

        assertTrue(result.isEmpty());
        verify(productService).getAllGroupingProductConfigByProductId(Set.of());
        verifyNoInteractions(mapper);
    }

    @Test
    void load_serviceReturnsEmptyMap_returnsEmptyMapWithoutMapping() {
        when(productService.getAllGroupingProductConfigByProductId(Set.of(1L, 2L))).thenReturn(Mono.just(Collections.emptyMap()));

        Map<Long, List<ProductConfig>> result = loader.load(Set.of(1L, 2L)).toCompletableFuture().join();

        assertTrue(result.isEmpty());
        verifyNoInteractions(mapper);
    }

    @Test
    void load_serviceError_propagatesAsFailedFuture() {
        var boom = new IllegalStateException("db down");
        when(productService.getAllGroupingProductConfigByProductId(Set.of(1L))).thenReturn(Mono.error(boom));

        CompletionException thrown = assertThrows(CompletionException.class,
                () -> loader.load(Set.of(1L)).toCompletableFuture().join());

        assertSame(boom, thrown.getCause());
        verifyNoInteractions(mapper);
    }
}
