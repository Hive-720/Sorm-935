package com.grunvald.sorm.service.whmcsdata.dataservice.order;

import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.dto.OrderSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.OrderFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.Order;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface OrderService {

    Mono<List<Order>> getAllOrderByIds(Set<Long> ids);

    Mono<Boolean> saveOrUpdateOrderAll(List<Order> orders);

    Mono<Boolean> saveOrUpdateOrRemoveOrderItemsAll(List<OrderItem> orderItems);

    Mono<List<OrderItem>> getAllOrderItemsByOrderIds(Set<Long> orderIds);

    Mono<OrderSearchResult> searchOrders(OrderFilter filter, String cursor, Integer offset, Integer limit);
}