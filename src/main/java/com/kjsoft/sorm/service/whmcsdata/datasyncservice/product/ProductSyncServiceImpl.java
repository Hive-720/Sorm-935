package com.kjsoft.sorm.service.whmcsdata.datasyncservice.product;

import com.kjsoft.sorm.service.whmcsdata.dataservice.product.ProductService;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.Product;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfig;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfigRelation;
import com.kjsoft.sorm.service.whmcsprovider.dto.mapper.ProductConfigMapper;
import com.kjsoft.sorm.service.whmcsprovider.dto.mapper.ProductConfigOptionMapper;
import com.kjsoft.sorm.service.whmcsprovider.dto.mapper.ProductMapper;
import com.kjsoft.sorm.service.whmcsprovider.WhmcsProvider;
import com.kjsoft.sorm.service.whmcsprovider.dto.ProductProviderDto;
import com.kjsoft.sorm.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@ConditionalOnBean(WhmcsProvider.class)
public class ProductSyncServiceImpl implements ProductSyncService {
    private final ProductService productService;
    private final WhmcsProvider whmcs;
    private final ProductMapper productMapper;
    private final ProductConfigMapper productConfigMapper;
    private final ProductConfigOptionMapper productConfigOptionMapper;
    private final DateUtils date;


    @Override
    public Mono<ProductSync> getActualProducts() {
        var productFlux = whmcs.getProducts();
        return Mono.zip(
                        collectProduct(productFlux),
                        collectProductConfig(productFlux),
                        collectProductConfigRelations(productFlux),
                        collectProductConfigOptions(productFlux)
                )
                .map(t4 -> ProductSync.builder()
                        .products(t4.getT1())
                        .productConfigs(new ArrayList<>(t4.getT2()))
                        .productConfigRelations(t4.getT3())
                        .productConfigOptions(new ArrayList<>(t4.getT4()))
                        .build()
                );
    }

    @Override
    @Transactional
    public Mono<Boolean> applyActualData(ProductSync data) {
        return productService.saveOrUpdateAllProduct(data.getProducts())
                .then(productService.saveOrUpdateAllProductConfig(data.getProductConfigs()))
                .then(productService.saveOrUpdateAllProductConfigOptions(data.getProductConfigOptions()))
                .then(productService.saveOrRemoveAllProductConfigRelations(data.getProductConfigRelations()));
    }

    private Mono<List<Product>> collectProduct(Flux<ProductProviderDto> productFlux) {
        return productFlux
                .map(productMapper::map)
                .collectList();
    }

    private Mono<Set<ProductConfig>> collectProductConfig(Flux<ProductProviderDto> productFlux) {
        return productFlux
                .flatMap(productProviderDto -> Flux.fromIterable(productProviderDto.getConfigs()))
                .map(productConfigMapper::map)
                .collect(Collectors.toSet());
    }

    private Mono<Set<ProductConfigOption>> collectProductConfigOptions(Flux<ProductProviderDto> productFlux) {
        return productFlux
                .flatMap(productProviderDto -> Flux.fromIterable(productProviderDto.getConfigs()))
                .flatMap(config -> Flux.fromIterable(config.getOptions())
                        .map(option -> productConfigOptionMapper.map(option, config.getId()))
                )
                .collect(Collectors.toSet());
    }

    private Mono<List<ProductConfigRelation>> collectProductConfigRelations(Flux<ProductProviderDto> productFlux) {
        return productFlux
                .flatMap(productProviderDto ->
                        Flux.fromIterable(productProviderDto.getConfigs())
                                .map(config -> new ProductConfigRelation(
                                        productProviderDto.getId(),
                                        config.getId(),
                                        date.now())
                                )
                )
                .collectList();
    }
}