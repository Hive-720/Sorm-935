package com.kjsoft.sorm.presentation.graphqlapi.operation.batchloader;

import com.kjsoft.sorm.service.whmcsdata.dataservice.product.ProductService;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.ProductConfig;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ProductConfigPresentationMapper;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class ProductConfigBatchLoader implements MappedBatchLoader<Long, List<ProductConfig>> {

    private final ProductService productService;
    private final ProductConfigPresentationMapper mapper;


    @NonNull
    @Override
    public CompletionStage<Map<Long, List<ProductConfig>>> load(@NonNull Set<Long> productIds) {
        return productService.getAllGroupingProductConfigByProductId(productIds)
                .map(productConfigMap -> productConfigMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().stream()
                                        .map(mapper::map)
                                        .collect(Collectors.toList())
                        ))
                )
                .toFuture();
    }
}