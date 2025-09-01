package com.kjsoft.sorm.service.whmcsdata.datasyncservice.order;

import com.kjsoft.sorm.service.whmcsdata.dataservice.order.OrderService;
import com.kjsoft.sorm.service.whmcsdata.dataservice.order.model.Order;
import com.kjsoft.sorm.service.whmcsdata.dataservice.order.model.OrderItem;
import com.kjsoft.sorm.service.whmcsprovider.dto.mapper.OrderItemMapper;
import com.kjsoft.sorm.service.whmcsprovider.dto.mapper.OrderMapper;
import com.kjsoft.sorm.service.whmcsprovider.WhmcsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;


@Service
@RequiredArgsConstructor
@ConditionalOnBean(WhmcsProvider.class)
public class OrderSyncServiceImpl implements OrderSyncService {
    private final OrderService orderService;
    private final WhmcsProvider whmcs;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;


    @Override
    public Mono<OrderSyncPage> getActualData(long offset, int limit, OffsetDateTime historyDepthDate) {
        return whmcs.getOrders(offset, limit, historyDepthDate)
                .flatMap(result -> Flux.fromIterable(result.getElements())
                        .flatMap(order -> Flux.fromIterable(order.getLineItems())
                                .map(item -> orderItemMapper.map(item, order.getId()))
                                .collectList()
                                .map(itemList -> Tuples.of(orderMapper.map(order), itemList))
                        )
                        .collect(() -> Tuples.of(new ArrayList<Order>(), new HashSet<OrderItem>()),
                                (collectT2, t2) -> {
                                    collectT2.getT1().add(t2.getT1());
                                    collectT2.getT2().addAll(t2.getT2());
                                })
                        .map(t2 -> OrderSyncPage.builder()
                                .orders(t2.getT1())
                                .orderItems(new ArrayList<>(t2.getT2()))
                                .hasNextPage(result.isHasNextPage())
                                .nextOffset(result.getNextOffset())
                                .build()
                        )
                );
    }

    @Override
    @Transactional
    public Mono<Boolean> applyActualData(OrderSyncPage page) {
        return orderService.saveOrUpdateOrderAll(page.getOrders())
                .then(orderService.saveOrUpdateOrRemoveOrderItemsAll(page.getOrderItems()));
    }
}