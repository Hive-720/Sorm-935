package com.grunvald.sorm.service.whmcsdata.dataservice.order;

import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.dto.OrderSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.OrderFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface OrderRepository {

    Mono<Boolean> addAllOrder(List<Order> orders);

    Mono<Boolean> updateAllOrder(List<Order> orders);

    Mono<Boolean> addAllOrderItem(List<OrderItem> orderItems);

    Mono<Boolean> updateAllOrderItem(List<OrderItem> orderItems);

    Mono<Boolean> deleteAllOrderItemByIdIn(Set<Long> orderItemIds);

    Flux<Order> findAllOrderByIdIn(Set<Long> ids);

    Flux<OrderItem> findAllOrderItemByOrderIds(Set<Long> orderIds);

    Mono<OrderSearchResult> findAllOrderByFilter(OrderFilter filter, String cursor, Integer offset, Integer limit);
}