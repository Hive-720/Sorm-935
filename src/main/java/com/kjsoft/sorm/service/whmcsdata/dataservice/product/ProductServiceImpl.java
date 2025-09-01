package com.kjsoft.sorm.service.whmcsdata.dataservice.product;

import com.kjsoft.sorm.service.whmcsdata.filter.ProductFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.Product;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfig;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.dto.ProductSearchResult;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfigRelation;
import com.kjsoft.sorm.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final DateUtils date;

    @Override
    public Mono<ProductSearchResult> search(ProductFilter filter, String cursor, Integer offset, Integer limit) {
        return repository.findAllByFilter(filter, cursor, offset, limit);
    }

    @Override
    public Mono<List<Product>> getAllProductByIds(@NonNull Set<Long> ids) {
        if (ids.isEmpty()) {
            return Mono.just(List.of());
        }

        return repository.findAllProductByIdIn(ids)
                .collectList();
    }

    @Override
    public Mono<Map<Long, List<ProductConfig>>> getAllGroupingProductConfigByProductId(@NonNull Set<Long> productIds) {
        if (productIds.isEmpty()) {
            return Mono.just(Collections.emptyMap());
        }

        return repository.findAllProductConfigRelationByProductIdIn(productIds)
                .collectMultimap(
                        ProductConfigRelation::getProductId,
                        ProductConfigRelation::getProductConfigId
                )
                .flatMap(productToProdutConfigIdsMap -> {
                    if (productToProdutConfigIdsMap.isEmpty()) {
                        return Mono.just(Collections.emptyMap());
                    }

                    var productConfigIds = productToProdutConfigIdsMap.values().stream()
                            .flatMap(Collection::stream)
                            .collect(Collectors.toSet());

                    return repository.findAllProductConfigsByIdIn(productConfigIds)
                            .collectMap(ProductConfig::getId, Function.identity())
                            .map(productConfigMap -> productToProdutConfigIdsMap.entrySet().stream()
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            entry -> entry.getValue().stream()
                                                    .map(productConfigMap::get)
                                                    .filter(Objects::nonNull)
                                                    .collect(Collectors.toList())
                                    )));
                });
    }

    @Override
    public Mono<List<ProductConfigOption>> getAllProductConfigOptionByProductConfigIds(@NonNull Set<Long> productConfigIds) {
        if (productConfigIds.isEmpty()) {
            return Mono.just(List.of());
        }

        return repository.findAllProductConfigOptionByProductConfigIdIn(productConfigIds)
                .collectList();
    }

    @Override
    @Transactional
    public Mono<Boolean> saveOrUpdateAllProduct(@NonNull List<Product> incomingList) {
        if (incomingList.isEmpty()) {
            return Mono.just(true);
        }

        return repository.findAllProduct()
                .collectMap(Product::getId)
                .flatMap(existingMap -> {

                    var toSave = new ArrayList<Product>();
                    var toUpdate = new ArrayList<Product>();

                    for (var actual : incomingList) {
                        var local = existingMap.remove(actual.getId());

                        if (local == null) {
                            actual.setUpdatedAt(date.now());
                            toSave.add(actual);
                        } else {
                            if (local.update(actual)) {
                                local.setUpdatedAt(date.now());
                                toUpdate.add(local);
                            }
                        }
                    }

                    if (!existingMap.isEmpty()) {
                        existingMap.values()
                                .forEach(product -> {
                                    product.setDeleted(true);
                                    toUpdate.add(product);
                                });
                    }

                    return (toSave.isEmpty() ? Mono.just(true) : repository.addAllProduct(toSave))
                            .zipWith(toUpdate.isEmpty() ? Mono.just(true) : repository.updateAllProduct(toUpdate))
                            .then(Mono.just(true));
                });
    }

    @Override
    @Transactional
    public Mono<Boolean> saveOrUpdateAllProductConfig(@NonNull List<ProductConfig> incomingList) {
        if (incomingList.isEmpty()) {
            return Mono.just(true);
        }

        return repository.findAllProductConfig()
                .collectMap(ProductConfig::getId)
                .flatMap(existingMap -> {

                    var toSave = new ArrayList<ProductConfig>();
                    var toUpdate = new ArrayList<ProductConfig>();
                    Set<Long> toDelete = null;

                    for (var actual : incomingList) {
                        var local = existingMap.remove(actual.getId());

                        if (local == null) {
                            toSave.add(actual);
                        } else {
                            if (local.update(actual)) {
                                toUpdate.add(local);
                            }
                        }
                    }

                    if (!existingMap.isEmpty()) {
                        toDelete = existingMap.values()
                                .stream()
                                .map(ProductConfig::getId)
                                .collect(Collectors.toSet());
                    }

                    return (toSave.isEmpty() ? Mono.just(true) : repository.addAllConfig(toSave))
                            .zipWith(toUpdate.isEmpty() ? Mono.just(true) : repository.updateAllConfig(toUpdate))
                            .zipWith(toDelete == null ? Mono.just(true) : repository.deleteAllConfigByIdIn(toDelete))
                            .then(Mono.just(true));
                });
    }

    @Override
    @Transactional
    public Mono<Boolean> saveOrUpdateAllProductConfigOptions(@NonNull List<ProductConfigOption> incomingList) {
        if (incomingList.isEmpty()) {
            return Mono.just(true);
        }

        return repository.findAllProductConfigOption()
                .collectMap(ProductConfigOption::getId)
                .flatMap(existingMap -> {

                    var toSave = new ArrayList<ProductConfigOption>();
                    var toUpdate = new ArrayList<ProductConfigOption>();
                    Set<Long> toDelete = null;

                    for (var actual : incomingList) {
                        var local = existingMap.remove(actual.getId());

                        if (local == null) {
                            toSave.add(actual);
                        } else {
                            if (local.update(actual)) {
                                toUpdate.add(local);
                            }
                        }
                    }

                    if (!existingMap.isEmpty()) {
                        toDelete = existingMap.values()
                                .stream()
                                .map(ProductConfigOption::getId)
                                .collect(Collectors.toSet());
                    }

                    return (toSave.isEmpty() ? Mono.just(true) : repository.addAllConfigOption(toSave))
                            .zipWith(toUpdate.isEmpty() ? Mono.just(true) : repository.updateAllConfigOption(toUpdate))
                            .zipWith(toDelete == null ? Mono.just(true) : repository.deleteAllConfigOptionByIdIn(toDelete))
                            .then(Mono.just(true));
                });
    }

    @Override
    @Transactional
    public Mono<Boolean> saveOrRemoveAllProductConfigRelations(@NonNull List<ProductConfigRelation> incomingList) {
        if (incomingList.isEmpty()) {
            return Mono.just(true);
        }

        return repository.findAllProductConfigRelation()
                .collectList()
                .flatMap(existingSet -> {
                    var toDelete = existingSet.stream()
                            .filter(relation -> !incomingList.contains(relation))
                            .toList();

                    var toSave = incomingList.stream()
                            .filter(relation -> !existingSet.contains(relation))
                            .toList();

                    return (toSave.isEmpty() ? Mono.just(true) : repository.addAllProductConfigRelation(toSave))
                            .zipWith(toDelete.isEmpty() ? Mono.just(true) : repository.deleteAllProductConfigRelation(toDelete))
                            .then(Mono.just(true));
                });
    }
}