package com.kjsoft.sorm.service.whmcsdata.dataservice.product;

import com.kjsoft.sorm.service.whmcsdata.filter.ProductFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.Product;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfig;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.dto.ProductSearchResult;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfigRelation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface ProductRepository {

    Mono<Boolean> addAllProduct(List<Product> products);

    Mono<Boolean> updateAllProduct(List<Product> products);

    Mono<Boolean> addAllConfig(List<ProductConfig> productConfigs);

    Mono<Boolean> updateAllConfig(List<ProductConfig> productConfigs);

    Mono<Boolean> deleteAllConfigByIdIn(Set<Long> productConfigIds);

    Mono<Boolean> addAllConfigOption(List<ProductConfigOption> productConfigOptions);

    Mono<Boolean> updateAllConfigOption(List<ProductConfigOption> productConfigOptions);

    Mono<Boolean> deleteAllConfigOptionByIdIn(Set<Long> productConfigOptionIds);

    Mono<Boolean> addAllProductConfigRelation(List<ProductConfigRelation> relations);

    Mono<Boolean> deleteAllProductConfigRelation(List<ProductConfigRelation> relations);

    Mono<ProductSearchResult> findAllByFilter(ProductFilter filter, String cursor, Integer offset, Integer limit);

    Flux<Product> findAllProduct();

    Flux<ProductConfig> findAllProductConfig();

    Flux<ProductConfig> findAllProductConfigsByIdIn(Set<Long> productConfigIds);

    Flux<ProductConfigOption> findAllProductConfigOption();

    Flux<Product> findAllProductByIdIn(Set<Long> ids);

    Flux<ProductConfigOption> findAllProductConfigOptionByProductConfigIdIn(Set<Long> productConfigIds);

    Flux<ProductConfigRelation> findAllProductConfigRelationByProductIdIn(Set<Long> productIds);

    Flux<ProductConfigRelation> findAllProductConfigRelation();
}