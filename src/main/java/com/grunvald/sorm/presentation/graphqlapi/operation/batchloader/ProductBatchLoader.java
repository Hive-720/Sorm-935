package com.grunvald.sorm.presentation.graphqlapi.operation.batchloader;

import com.grunvald.sorm.service.whmcsdata.dataservice.product.ProductService;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ProductPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Product;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;


@Component
@RequiredArgsConstructor
public class ProductBatchLoader implements MappedBatchLoader<Long, Product> {
    private final ProductService service;
    private final ProductPresentationMapper mapper;


    @NonNull
    @Override
    public CompletionStage<Map<Long, Product>> load(@NonNull Set<Long> productIds) {
        return service.getAllProductByIds(productIds)
                .flatMapMany(Flux::fromIterable)
                .map(mapper::map)
                .collectMap(Product::id)
                .toFuture();
    }
}