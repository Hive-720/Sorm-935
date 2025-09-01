package com.kjsoft.sorm.presentation.graphqlapi.operation.queryresolver;

import com.kjsoft.sorm.service.whmcsdata.dataservice.product.ProductService;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.ProductFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.ProductFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Product;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.ProductConfig;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.ProductConfigOption;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ProductResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ProductResultMapper;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class ProductResolver {
    private final ProductService productService;
    private final ProductFilterPresentationMapper productFilterMapper;
    private final ProductResultMapper productResultMapper;


    @SchemaMapping(typeName = "QueryMessage")
    public Mono<ProductResult> products(@Argument ProductFilterInput filter, @Argument Integer offset,
                                        @Argument Integer limit, @Argument String cursor) {
        var searchFilter = productFilterMapper.map(filter);
        return productService.search(searchFilter, cursor, offset, limit)
                .map(productResultMapper::map);
    }

    @SchemaMapping(typeName = "Product", field = "configs")
    public Mono<List<ProductConfig>> getProductConfigsForProduct(Product product,
                                                                 DataLoader<Long, List<ProductConfig>> dataLoader) {
        return Mono.fromFuture(dataLoader.load(product.id()));
    }

    @SchemaMapping(typeName = "ProductConfig", field = "options")
    public Mono<List<ProductConfigOption>> getOptionsForProductConfig(ProductConfig productConfig,
                                                                      DataLoader<Long, List<ProductConfigOption>> dataLoader) {
        return Mono.fromFuture(dataLoader.load(productConfig.id()));
    }
}