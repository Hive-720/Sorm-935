package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ProductFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Product;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ProductConfig;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ProductConfigOption;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.ProductService;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.dto.ProductSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.ProductFilter;
import com.grunvald.sorm.testutil.MapStructTestFactory;
import org.dataloader.DataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductResolverTest {

    @Mock
    private ProductService productService;

    @Mock
    private DataLoader<Long, List<ProductConfig>> configsDataLoader;

    @Mock
    private DataLoader<Long, List<ProductConfigOption>> optionsDataLoader;

    private final FilterMappers filterMappers = new FilterMappers();

    private ProductResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new ProductResolver(productService, filterMappers.productMapper,
                MapStructTestFactory.productResultMapper());
    }

    private ProductFilterInput sampleFilter() {
        ProductFilterInput nestedAnd = new ProductFilterInput(
                SampleFilters.dateTime(SampleFilters.D11), null, null, null);
        ProductFilterInput nestedOr = new ProductFilterInput(
                SampleFilters.dateTime(SampleFilters.D12), null, null, null);
        ProductFilterInput nestedNot = new ProductFilterInput(
                SampleFilters.dateTime(SampleFilters.D13), null, null, null);
        return new ProductFilterInput(
                SampleFilters.dateTime(SampleFilters.D09),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);
    }

    @Test
    void products_mapsFilterAndPassesPaginationArgumentsInOrder() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product();
        model.setId(1L);
        model.setName("VPS-1");
        model.setDescription("Virtual server");
        model.setPayType("recurring");
        when(productService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new ProductSearchResult("cur-13", true, List.of(model))));

        StepVerifier.create(resolver.products(sampleFilter(), 7, 77, "cur-13"))
                .assertNext(result -> {
                    assertEquals("cur-13", result.cursor());
                    assertTrue(result.hasNextPage());
                    assertEquals(1, result.result().size());
                    Product first = result.result().getFirst();
                    assertEquals(1L, first.id());
                    assertEquals("VPS-1", first.name());
                    assertEquals("Virtual server", first.description());
                    assertEquals("recurring", first.payType());
                })
                .verifyComplete();

        ArgumentCaptor<ProductFilter> filterCaptor = ArgumentCaptor.forClass(ProductFilter.class);
        verify(productService).search(filterCaptor.capture(), eq("cur-13"), eq(7), eq(77));
        ProductFilter filter = filterCaptor.getValue();
        assertNotNull(filter.getUpdatedAt());
        assertEquals(OffsetDateTime.parse(SampleFilters.D09), filter.getUpdatedAt().getUtc().getEq());
        assertEquals(1, filter.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), filter.getAnd().getFirst().getUpdatedAt().getUtc().getEq());
        assertEquals(1, filter.getOr().size());
        assertNotNull(filter.getNot());
    }

    @Test
    void products_passesNullArgumentsThrough() {
        when(productService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new ProductSearchResult(null, false, List.of())));

        StepVerifier.create(resolver.products(null, null, null, null))
                .assertNext(result -> {
                    assertNull(result.cursor());
                    assertFalse(result.hasNextPage());
                    assertEquals(0, result.result().size());
                })
                .verifyComplete();

        verify(productService).search(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void products_propagatesServiceError() {
        when(productService.search(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("product boom")));

        StepVerifier.create(resolver.products(sampleFilter(), 0, 10, null))
                .expectErrorSatisfies(e -> assertEquals("product boom", e.getMessage()))
                .verify();
    }

    @Test
    void products_emptyServiceResultCompletesEmpty() {
        when(productService.search(any(), any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(resolver.products(sampleFilter(), 0, 10, null))
                .verifyComplete();
    }

    @Test
    void getProductConfigsForProduct_loadsConfigsByProductId() {
        List<ProductConfig> configs = List.of(new ProductConfig(11L, "cpu", "dropdown"));
        when(configsDataLoader.load(1L)).thenReturn(CompletableFuture.completedFuture(configs));

        Product product = new Product(1L, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getProductConfigsForProduct(product, configsDataLoader))
                .expectNext(configs)
                .verifyComplete();

        verify(configsDataLoader).load(1L);
    }

    @Test
    void getProductConfigsForProduct_nullResultCompletesEmpty() {
        when(configsDataLoader.load(1L)).thenReturn(CompletableFuture.completedFuture(null));

        Product product = new Product(1L, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getProductConfigsForProduct(product, configsDataLoader))
                .verifyComplete();
    }

    @Test
    void getOptionsForProductConfig_loadsOptionsByConfigId() {
        List<ProductConfigOption> options = List.of(new ProductConfigOption(21L, 11L, "2 cores", null));
        when(optionsDataLoader.load(11L)).thenReturn(CompletableFuture.completedFuture(options));

        ProductConfig config = new ProductConfig(11L, "cpu", "dropdown");

        StepVerifier.create(resolver.getOptionsForProductConfig(config, optionsDataLoader))
                .expectNext(options)
                .verifyComplete();

        verify(optionsDataLoader).load(11L);
    }

    @Test
    void getOptionsForProductConfig_nullResultCompletesEmpty() {
        when(optionsDataLoader.load(11L)).thenReturn(CompletableFuture.completedFuture(null));

        ProductConfig config = new ProductConfig(11L, "cpu", "dropdown");

        StepVerifier.create(resolver.getOptionsForProductConfig(config, optionsDataLoader))
                .verifyComplete();
    }
}
