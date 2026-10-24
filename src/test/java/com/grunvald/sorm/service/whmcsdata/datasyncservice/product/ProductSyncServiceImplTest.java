package com.grunvald.sorm.service.whmcsdata.datasyncservice.product;

import com.grunvald.sorm.service.whmcsdata.dataservice.product.ProductService;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfig;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigRelation;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.ProductProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.ProductConfigMapper;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.ProductConfigOptionMapper;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.ProductMapper;
import com.grunvald.sorm.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductSyncServiceImplTest {

    @Mock
    private ProductService productService;
    @Mock
    private WhmcsProvider whmcs;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductConfigMapper productConfigMapper;
    @Mock
    private ProductConfigOptionMapper productConfigOptionMapper;
    @Mock
    private DateUtils date;

    private ProductSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProductSyncServiceImpl(productService, whmcs, productMapper,
                productConfigMapper, productConfigOptionMapper, date);
    }

    private ProductProviderDto.Config config(long id, List<ProductProviderDto.Config.Option> options) {
        var c = new ProductProviderDto.Config();
        c.setId(id);
        c.setOptions(options);
        return c;
    }

    private ProductProviderDto.Config.Option option(int id) {
        var o = new ProductProviderDto.Config.Option();
        o.setId(id);
        return o;
    }

    @Test
    void getActualProducts_collectsAllFourLists() {
        var now = OffsetDateTime.parse("2026-01-02T03:04:05Z");
        var optionDto = option(1);
        var configDto = config(10L, List.of(optionDto));
        var productDto = ProductProviderDto.builder()
                .id(100L)
                .configs(List.of(configDto))
                .build();

        var mappedProduct = new Product();
        mappedProduct.setId(100L);
        var mappedConfig = new ProductConfig(10L, "cfg", "type");
        var mappedOption = new ProductConfigOption();
        mappedOption.setId(1L);
        mappedOption.setProductConfigId(10L);

        when(whmcs.getProducts()).thenReturn(Flux.just(productDto));
        when(date.now()).thenReturn(now);
        when(productMapper.map(productDto)).thenReturn(mappedProduct);
        when(productConfigMapper.map(configDto)).thenReturn(mappedConfig);
        when(productConfigOptionMapper.map(optionDto, 10L)).thenReturn(mappedOption);

        StepVerifier.create(service.getActualProducts())
                .assertNext(sync -> {
                    assertEquals(List.of(mappedProduct), sync.getProducts());
                    assertEquals(List.of(mappedConfig), sync.getProductConfigs());
                    assertEquals(List.of(mappedOption), sync.getProductConfigOptions());
                    assertEquals(1, sync.getProductConfigRelations().size());
                    var relation = sync.getProductConfigRelations().get(0);
                    assertEquals(100L, relation.getProductId());
                    assertEquals(10L, relation.getProductConfigId());
                    assertEquals(now, relation.getCreatedAt());
                })
                .verifyComplete();
    }

    @Test
    void getActualProducts_emptySource_returnsEmptyLists() {
        when(whmcs.getProducts()).thenReturn(Flux.empty());

        StepVerifier.create(service.getActualProducts())
                .assertNext(sync -> {
                    assertTrue(sync.getProducts().isEmpty());
                    assertTrue(sync.getProductConfigs().isEmpty());
                    assertTrue(sync.getProductConfigOptions().isEmpty());
                    assertTrue(sync.getProductConfigRelations().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void applyActualData_chainsAllFourSaveOperations() {
        var data = ProductSync.builder()
                .products(List.of())
                .productConfigs(List.of())
                .productConfigOptions(List.of())
                .productConfigRelations(List.of())
                .build();
        when(productService.saveOrUpdateAllProduct(anyList())).thenReturn(Mono.just(true));
        when(productService.saveOrUpdateAllProductConfig(anyList())).thenReturn(Mono.just(true));
        when(productService.saveOrUpdateAllProductConfigOptions(anyList())).thenReturn(Mono.just(true));
        when(productService.saveOrRemoveAllProductConfigRelations(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.applyActualData(data))
                .expectNext(true)
                .verifyComplete();

        verify(productService).saveOrUpdateAllProduct(data.getProducts());
        verify(productService).saveOrUpdateAllProductConfig(data.getProductConfigs());
        verify(productService).saveOrUpdateAllProductConfigOptions(data.getProductConfigOptions());
        verify(productService).saveOrRemoveAllProductConfigRelations(data.getProductConfigRelations());
    }
}
