package com.kjsoft.sorm.service.whmcsdata.dataservice.product;

import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfigRelation;
import com.kjsoft.sorm.service.whmcsdata.filter.ProductFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.Product;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfig;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.dto.ProductSearchResult;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface ProductService {

    Mono<ProductSearchResult> search(ProductFilter filter, String cursor, Integer offset, Integer limit);

    Mono<List<Product>> getAllProductByIds(Set<Long> ids);

    Mono<Map<Long, List<ProductConfig>>> getAllGroupingProductConfigByProductId(Set<Long> productIds);

    Mono<List<ProductConfigOption>> getAllProductConfigOptionByProductConfigIds(Set<Long> configProductIds);

    Mono<Boolean> saveOrUpdateAllProduct(List<Product> products);

    Mono<Boolean> saveOrUpdateAllProductConfig(List<ProductConfig> productConfigs);

    Mono<Boolean> saveOrUpdateAllProductConfigOptions(List<ProductConfigOption> productConfigOptions);

    Mono<Boolean> saveOrRemoveAllProductConfigRelations(List<ProductConfigRelation> relations);
}