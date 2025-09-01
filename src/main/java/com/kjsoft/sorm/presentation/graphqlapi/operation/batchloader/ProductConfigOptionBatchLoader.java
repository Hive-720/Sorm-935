package com.kjsoft.sorm.presentation.graphqlapi.operation.batchloader;


import com.kjsoft.sorm.service.whmcsdata.dataservice.product.ProductService;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.ProductConfigOption;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ProductConfigOptionPresentationMapper;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class ProductConfigOptionBatchLoader implements MappedBatchLoader<Long, List<ProductConfigOption>> {
    private final ProductService service;
    private final ProductConfigOptionPresentationMapper mapper;


    @NonNull
    @Override
    public CompletionStage<Map<Long, List<ProductConfigOption>>> load(@NonNull Set<Long> productConfigIds) {
        return service.getAllProductConfigOptionByProductConfigIds(productConfigIds)
                .flatMapMany(Flux::fromIterable)
                .map(mapper::map)
                .collect(Collectors.groupingBy(
                                ProductConfigOption::productConfigId,
                                Collectors.toList()
                        )
                )
                .toFuture();
    }
}