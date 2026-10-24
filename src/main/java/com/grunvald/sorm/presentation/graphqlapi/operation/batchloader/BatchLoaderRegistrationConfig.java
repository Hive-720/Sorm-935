package com.grunvald.sorm.presentation.graphqlapi.operation.batchloader;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.*;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.MappedBatchLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.graphql.execution.DefaultBatchLoaderRegistry;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Регистрация всех MappedBatchLoader в BatchLoaderRegistry Spring GraphQL.
 * Резолверы получают DataLoader по имени value-типа (Client.class.getName() и т.п.),
 * а для DataLoader&lt;Long, List&lt;...&gt;&gt; - по имени параметра метода, поэтому
 * List-лоадеры регистрируются с уникальными именами, совпадающими с именами параметров.
 */
@Configuration
public class BatchLoaderRegistrationConfig {

    @Bean
    public BatchLoaderRegistry batchLoaderRegistry(ClientBatchLoader clientBatchLoader,
                                                   UserBatchLoader userBatchLoader,
                                                   ClientGroupBatchLoader clientGroupBatchLoader,
                                                   ProductBatchLoader productBatchLoader,
                                                   ProductConfigBatchLoader productConfigBatchLoader,
                                                   ProductConfigOptionBatchLoader productConfigOptionBatchLoader,
                                                   OrderBatchLoader orderBatchLoader,
                                                   OrderItemBatchLoader orderItemBatchLoader,
                                                   InvoiceBatchLoader invoiceBatchLoader,
                                                   ContactBatchLoader contactBatchLoader,
                                                   AffiliateBatchLoader affiliateBatchLoader,
                                                   TransactionBatchLoader transactionBatchLoader,
                                                   ClientsByUserBatchLoader clientsByUserBatchLoader) {
        var registry = new DefaultBatchLoaderRegistry();

        registry.forTypePair(Long.class, Client.class).registerMappedBatchLoader(adapt(clientBatchLoader));
        registry.forTypePair(Long.class, User.class).registerMappedBatchLoader(adapt(userBatchLoader));
        registry.forTypePair(Long.class, ClientGroup.class).registerMappedBatchLoader(adapt(clientGroupBatchLoader));
        registry.forTypePair(Long.class, Product.class).registerMappedBatchLoader(adapt(productBatchLoader));
        registry.forTypePair(Long.class, Order.class).registerMappedBatchLoader(adapt(orderBatchLoader));
        registry.forTypePair(Long.class, Invoice.class).registerMappedBatchLoader(adapt(invoiceBatchLoader));
        registry.forTypePair(Long.class, Contact.class).registerMappedBatchLoader(adapt(contactBatchLoader));
        registry.forTypePair(Long.class, Affiliate.class).registerMappedBatchLoader(adapt(affiliateBatchLoader));
        registry.forTypePair(Long.class, Transaction.class).registerMappedBatchLoader(adapt(transactionBatchLoader));

        registry.forTypePair(Long.class, List.class).withName("orderItemsLoader")
                .registerMappedBatchLoader(adaptList(orderItemBatchLoader));
        registry.forTypePair(Long.class, List.class).withName("clientsLoader")
                .registerMappedBatchLoader(adaptList(clientsByUserBatchLoader));
        registry.forTypePair(Long.class, List.class).withName("productConfigsLoader")
                .registerMappedBatchLoader(adaptList(productConfigBatchLoader));
        registry.forTypePair(Long.class, List.class).withName("productConfigOptionsLoader")
                .registerMappedBatchLoader(adaptList(productConfigOptionBatchLoader));

        return registry;
    }

    private static <K, V> BiFunction<Set<K>, BatchLoaderEnvironment, Mono<Map<K, V>>> adapt(
            MappedBatchLoader<K, V> loader) {
        return (keys, environment) -> Mono.fromFuture(loader.load(keys).toCompletableFuture());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static BiFunction<Set<Long>, BatchLoaderEnvironment, Mono<Map<Long, List>>> adaptList(
            MappedBatchLoader<Long, ?> loader) {
        return (keys, environment) -> (Mono) Mono.fromFuture(
                ((MappedBatchLoader) loader).load(keys).toCompletableFuture());
    }
}
