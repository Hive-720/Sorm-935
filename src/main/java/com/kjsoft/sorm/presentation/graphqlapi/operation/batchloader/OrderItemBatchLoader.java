package com.kjsoft.sorm.presentation.graphqlapi.operation.batchloader;

import com.kjsoft.sorm.service.whmcsdata.dataservice.order.OrderService;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.OrderItem;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.OrderItemPresentationMapper;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class OrderItemBatchLoader implements MappedBatchLoader<Long, List<OrderItem>> {
    private final OrderService orderService;
    private final OrderItemPresentationMapper mapper;


    @NonNull
    @Override
    public CompletionStage<Map<Long, List<OrderItem>>> load(@NonNull Set<Long> orderIds) {
        return orderService.getAllOrderItemsByOrderIds(orderIds)
                .flatMapMany(Flux::fromIterable)
                .map(mapper::map)
                .collect(Collectors.groupingBy(
                                OrderItem::orderId,
                                Collectors.toList()
                        )
                )
                .toFuture();
    }
}