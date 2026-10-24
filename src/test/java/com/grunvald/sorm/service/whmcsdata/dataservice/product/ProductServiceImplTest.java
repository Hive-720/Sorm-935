package com.grunvald.sorm.service.whmcsdata.dataservice.product;

import com.grunvald.sorm.service.whmcsdata.dataservice.product.dto.ProductSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfig;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigRelation;
import com.grunvald.sorm.service.whmcsdata.filter.ProductFilter;
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
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;
    @Mock
    private DateUtils date;

    @Captor
    private ArgumentCaptor<List<Product>> productListCaptor;
    @Captor
    private ArgumentCaptor<List<ProductConfig>> configListCaptor;
    @Captor
    private ArgumentCaptor<List<ProductConfigOption>> optionListCaptor;
    @Captor
    private ArgumentCaptor<Set<Long>> idSetCaptor;
    @Captor
    private ArgumentCaptor<List<ProductConfigRelation>> relationListCaptor;

    private ProductServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProductServiceImpl(repository, date);
    }

    private Product product(long id, String name) {
        var p = new Product();
        p.setId(id);
        p.setName(name);
        return p;
    }

    private ProductConfig config(long id, String name) {
        return new ProductConfig(id, name, "type");
    }

    private ProductConfigOption option(long id, long configId, String name) {
        var o = new ProductConfigOption();
        o.setId(id);
        o.setProductConfigId(configId);
        o.setName(name);
        return o;
    }

    @Test
    void search_delegatesToRepository() {
        var filter = new ProductFilter();
        var result = new ProductSearchResult("c", false, List.of());
        when(repository.findAllByFilter(filter, "c", 0, 25)).thenReturn(Mono.just(result));

        StepVerifier.create(service.search(filter, "c", 0, 25))
                .expectNext(result)
                .verifyComplete();
    }

    @Test
    void getAllProductByIds_emptySet_shortCircuits() {
        StepVerifier.create(service.getAllProductByIds(Set.of()))
                .expectNext(List.of())
                .verifyComplete();

        verify(repository, never()).findAllProductByIdIn(anySet());
    }

    @Test
    void getAllProductByIds_collectsFromRepository() {
        var products = List.of(product(1, "p1"), product(2, "p2"));
        when(repository.findAllProductByIdIn(Set.of(1L, 2L))).thenReturn(Flux.fromIterable(products));

        StepVerifier.create(service.getAllProductByIds(Set.of(1L, 2L)))
                .expectNext(products)
                .verifyComplete();
    }

    @Test
    void getAllGroupingProductConfigByProductId_emptyIds_returnsEmptyMap() {
        StepVerifier.create(service.getAllGroupingProductConfigByProductId(Set.of()))
                .expectNext(Map.of())
                .verifyComplete();

        verify(repository, never()).findAllProductConfigRelationByProductIdIn(anySet());
    }

    @Test
    void getAllGroupingProductConfigByProductId_noRelations_returnsEmptyMap() {
        when(repository.findAllProductConfigRelationByProductIdIn(Set.of(1L))).thenReturn(Flux.empty());

        StepVerifier.create(service.getAllGroupingProductConfigByProductId(Set.of(1L)))
                .expectNext(Map.of())
                .verifyComplete();

        verify(repository, never()).findAllProductConfigsByIdIn(anySet());
    }

    @Test
    void getAllGroupingProductConfigByProductId_groupsConfigs() {
        when(repository.findAllProductConfigRelationByProductIdIn(Set.of(1L, 2L))).thenReturn(Flux.just(
                new ProductConfigRelation(1L, 10L, null),
                new ProductConfigRelation(1L, 11L, null),
                new ProductConfigRelation(2L, 10L, null)
        ));
        when(repository.findAllProductConfigsByIdIn(Set.of(10L, 11L))).thenReturn(Flux.just(
                config(10, "c10"), config(11, "c11")
        ));

        StepVerifier.create(service.getAllGroupingProductConfigByProductId(Set.of(1L, 2L)))
                .assertNext(map -> {
                    assertEquals(2, map.size());
                    assertEquals(Set.of(10L, 11L), map.get(1L).stream().map(ProductConfig::getId).collect(java.util.stream.Collectors.toSet()));
                    assertEquals(List.of(10L), map.get(2L).stream().map(ProductConfig::getId).toList());
                })
                .verifyComplete();
    }

    @Test
    void getAllGroupingProductConfigByProductId_skipsUnknownConfigs() {
        when(repository.findAllProductConfigRelationByProductIdIn(Set.of(1L))).thenReturn(Flux.just(
                new ProductConfigRelation(1L, 10L, null),
                new ProductConfigRelation(1L, 999L, null)
        ));
        when(repository.findAllProductConfigsByIdIn(Set.of(10L, 999L))).thenReturn(Flux.just(config(10, "c10")));

        StepVerifier.create(service.getAllGroupingProductConfigByProductId(Set.of(1L)))
                .assertNext(map -> assertEquals(List.of(10L), map.get(1L).stream().map(ProductConfig::getId).toList()))
                .verifyComplete();
    }

    @Test
    void getAllProductConfigOptionByProductConfigIds_emptySet_shortCircuits() {
        StepVerifier.create(service.getAllProductConfigOptionByProductConfigIds(Set.of()))
                .expectNext(List.of())
                .verifyComplete();

        verify(repository, never()).findAllProductConfigOptionByProductConfigIdIn(anySet());
    }

    @Test
    void getAllProductConfigOptionByProductConfigIds_collectsFromRepository() {
        var options = List.of(option(1, 10, "o1"));
        when(repository.findAllProductConfigOptionByProductConfigIdIn(Set.of(10L)))
                .thenReturn(Flux.fromIterable(options));

        StepVerifier.create(service.getAllProductConfigOptionByProductConfigIds(Set.of(10L)))
                .expectNext(options)
                .verifyComplete();
    }

    @Test
    void saveOrUpdateAllProduct_emptyList_returnsTrueWithoutRepositoryCalls() {
        StepVerifier.create(service.saveOrUpdateAllProduct(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).findAllProduct();
    }

    @Test
    void saveOrUpdateAllProduct_newProduct_addedWithTimestamp() {
        var now = OffsetDateTime.parse("2026-01-02T03:04:05Z");
        var incoming = product(1, "new");
        when(date.now()).thenReturn(now);
        when(repository.findAllProduct()).thenReturn(Flux.empty());
        when(repository.addAllProduct(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAllProduct(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAllProduct(productListCaptor.capture());
        assertEquals(1, productListCaptor.getValue().size());
        assertEquals(now, productListCaptor.getValue().get(0).getUpdatedAt());
        verify(repository, never()).updateAllProduct(anyList());
    }

    @Test
    void saveOrUpdateAllProduct_changedProduct_updatesLocalInstance() {
        var now = OffsetDateTime.parse("2026-01-02T03:04:05Z");
        var existing = product(1, "old");
        var incoming = product(1, "new");
        when(date.now()).thenReturn(now);
        when(repository.findAllProduct()).thenReturn(Flux.just(existing));
        when(repository.updateAllProduct(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAllProduct(List.of(incoming)))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAllProduct(anyList());
        verify(repository).updateAllProduct(productListCaptor.capture());
        assertEquals("new", productListCaptor.getValue().get(0).getName());
        assertEquals(now, productListCaptor.getValue().get(0).getUpdatedAt());
    }

    @Test
    void saveOrUpdateAllProduct_unchangedProduct_nothingSaved() {
        when(repository.findAllProduct()).thenReturn(Flux.just(product(1, "same")));

        StepVerifier.create(service.saveOrUpdateAllProduct(List.of(product(1, "same"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAllProduct(anyList());
        verify(repository, never()).updateAllProduct(anyList());
    }

    @Test
    void saveOrUpdateAllProduct_productsAbsentInIncoming_markedDeleted() {
        var stale = product(99, "stale");
        when(date.now()).thenReturn(OffsetDateTime.parse("2026-01-02T03:04:05Z"));
        when(repository.findAllProduct()).thenReturn(Flux.just(stale));
        when(repository.addAllProduct(anyList())).thenReturn(Mono.just(true));
        when(repository.updateAllProduct(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAllProduct(List.of(product(1, "new"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository).updateAllProduct(productListCaptor.capture());
        assertEquals(99L, productListCaptor.getValue().get(0).getId());
        assertTrue(productListCaptor.getValue().get(0).isDeleted());
    }

    @Test
    void saveOrUpdateAllProductConfig_emptyList_returnsTrueWithoutRepositoryCalls() {
        StepVerifier.create(service.saveOrUpdateAllProductConfig(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).findAllProductConfig();
    }

    @Test
    void saveOrUpdateAllProductConfig_newConfig_added() {
        when(repository.findAllProductConfig()).thenReturn(Flux.empty());
        when(repository.addAllConfig(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAllProductConfig(List.of(config(1, "c1"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAllConfig(configListCaptor.capture());
        assertEquals(1, configListCaptor.getValue().size());
        verify(repository, never()).updateAllConfig(anyList());
        verify(repository, never()).deleteAllConfigByIdIn(anySet());
    }

    @Test
    void saveOrUpdateAllProductConfig_changedConfig_updatesLocalInstance() {
        var existing = config(1, "old");
        when(repository.findAllProductConfig()).thenReturn(Flux.just(existing));
        when(repository.updateAllConfig(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAllProductConfig(List.of(config(1, "new"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository).updateAllConfig(configListCaptor.capture());
        assertEquals("new", configListCaptor.getValue().get(0).getName());
    }

    @Test
    void saveOrUpdateAllProductConfig_unchangedConfig_nothingSaved() {
        when(repository.findAllProductConfig()).thenReturn(Flux.just(config(1, "same")));

        StepVerifier.create(service.saveOrUpdateAllProductConfig(List.of(config(1, "same"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAllConfig(anyList());
        verify(repository, never()).updateAllConfig(anyList());
        verify(repository, never()).deleteAllConfigByIdIn(anySet());
    }

    @Test
    void saveOrUpdateAllProductConfig_configsAbsentInIncoming_deleted() {
        var stale = config(99, "stale");
        when(repository.findAllProductConfig()).thenReturn(Flux.just(stale));
        when(repository.addAllConfig(anyList())).thenReturn(Mono.just(true));
        when(repository.deleteAllConfigByIdIn(anySet())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAllProductConfig(List.of(config(1, "new"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository).deleteAllConfigByIdIn(idSetCaptor.capture());
        assertEquals(Set.of(99L), idSetCaptor.getValue());
        verify(repository, never()).updateAllConfig(anyList());
    }

    @Test
    void saveOrUpdateAllProductConfigOptions_emptyList_returnsTrueWithoutRepositoryCalls() {
        StepVerifier.create(service.saveOrUpdateAllProductConfigOptions(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).findAllProductConfigOption();
    }

    @Test
    void saveOrUpdateAllProductConfigOptions_newOption_added() {
        when(repository.findAllProductConfigOption()).thenReturn(Flux.empty());
        when(repository.addAllConfigOption(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAllProductConfigOptions(List.of(option(1, 10, "o1"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAllConfigOption(optionListCaptor.capture());
        assertEquals(1, optionListCaptor.getValue().size());
    }

    @Test
    void saveOrUpdateAllProductConfigOptions_changedOption_updatesLocalInstance() {
        var existing = option(1, 10, "old");
        when(repository.findAllProductConfigOption()).thenReturn(Flux.just(existing));
        when(repository.updateAllConfigOption(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAllProductConfigOptions(List.of(option(1, 10, "new"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository).updateAllConfigOption(optionListCaptor.capture());
        assertEquals("new", optionListCaptor.getValue().get(0).getName());
    }

    @Test
    void saveOrUpdateAllProductConfigOptions_unchangedOption_nothingSaved() {
        when(repository.findAllProductConfigOption()).thenReturn(Flux.just(option(1, 10, "same")));

        StepVerifier.create(service.saveOrUpdateAllProductConfigOptions(List.of(option(1, 10, "same"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAllConfigOption(anyList());
        verify(repository, never()).updateAllConfigOption(anyList());
        verify(repository, never()).deleteAllConfigOptionByIdIn(anySet());
    }

    @Test
    void saveOrUpdateAllProductConfigOptions_optionsAbsentInIncoming_deleted() {
        var stale = option(99, 10, "stale");
        when(repository.findAllProductConfigOption()).thenReturn(Flux.just(stale));
        when(repository.addAllConfigOption(anyList())).thenReturn(Mono.just(true));
        when(repository.deleteAllConfigOptionByIdIn(anySet())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateAllProductConfigOptions(List.of(option(1, 10, "new"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository).deleteAllConfigOptionByIdIn(idSetCaptor.capture());
        assertEquals(Set.of(99L), idSetCaptor.getValue());
    }

    @Test
    void saveOrRemoveAllProductConfigRelations_emptyList_returnsTrueWithoutRepositoryCalls() {
        StepVerifier.create(service.saveOrRemoveAllProductConfigRelations(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).findAllProductConfigRelation();
    }

    @Test
    void saveOrRemoveAllProductConfigRelations_allExisting_nothingSaved() {
        var existing = List.of(new ProductConfigRelation(1L, 10L, null));
        when(repository.findAllProductConfigRelation()).thenReturn(Flux.fromIterable(existing));

        StepVerifier.create(service.saveOrRemoveAllProductConfigRelations(existing))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAllProductConfigRelation(anyList());
        verify(repository, never()).deleteAllProductConfigRelation(anyList());
    }

    @Test
    void saveOrRemoveAllProductConfigRelations_mixed_addsMissingAndDeletesGone() {
        var keep = new ProductConfigRelation(1L, 10L, null);
        var remove = new ProductConfigRelation(1L, 11L, null);
        var add = new ProductConfigRelation(2L, 10L, null);

        when(repository.findAllProductConfigRelation()).thenReturn(Flux.just(keep, remove));
        when(repository.addAllProductConfigRelation(anyList())).thenReturn(Mono.just(true));
        when(repository.deleteAllProductConfigRelation(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrRemoveAllProductConfigRelations(List.of(keep, add)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAllProductConfigRelation(relationListCaptor.capture());
        assertEquals(1, relationListCaptor.getValue().size());
        assertEquals(2L, relationListCaptor.getValue().get(0).getProductId());

        verify(repository).deleteAllProductConfigRelation(relationListCaptor.capture());
        assertEquals(1, relationListCaptor.getValue().size());
        assertEquals(11L, relationListCaptor.getValue().get(0).getProductConfigId());
    }
}
