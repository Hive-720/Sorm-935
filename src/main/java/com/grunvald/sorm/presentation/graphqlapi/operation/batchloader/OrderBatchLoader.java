package com.grunvald.sorm.presentation.graphqlapi.operation.batchloader;

import com.grunvald.sorm.service.whmcsdata.dataservice.order.OrderService;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.OrderPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Order;
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
public class OrderBatchLoader implements MappedBatchLoader<Long, Order> {
    private final OrderService service;
    private final OrderPresentationMapper mapper;


    @NonNull
    @Override
    public CompletionStage<Map<Long, Order>> load(@NonNull Set<Long> orderIds) {
        return service.getAllOrderByIds(orderIds)
                .flatMapMany(Flux::fromIterable)
                .map(mapper::map)
                .collectMap(Order::id)
                .toFuture();
    }
}