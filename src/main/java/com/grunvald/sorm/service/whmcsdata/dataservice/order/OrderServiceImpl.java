package com.grunvald.sorm.service.whmcsdata.dataservice.order;

import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.dto.OrderSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.OrderFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repository;


    @Override
    public Mono<List<Order>> getAllOrderByIds(@NonNull Set<Long> ids) {
        if (ids.isEmpty()) {
            return Mono.just(List.of());
        }

        return repository.findAllOrderByIdIn(ids)
                .collectList();
    }

    @Override
    @Transactional
    public Mono<Boolean> saveOrUpdateOrderAll(@NonNull List<Order> incomingList) {
        if (incomingList.isEmpty()) {
            return Mono.just(true);
        }

        var ids = extractIds(incomingList);

        return repository.findAllOrderByIdIn(ids)
                .collectMap(Order::getId)
                .flatMap(existingMap -> {

                    var toSave = new ArrayList<Order>();
                    var toUpdate = new ArrayList<Order>();

                    for (var actual : incomingList) {
                        var local = existingMap.get(actual.getId());

                        if (local == null) {
                            toSave.add(actual);
                        } else {
                            if (local.update(actual)) {
                                toUpdate.add(local);
                            }
                        }
                    }

                    return (toSave.isEmpty() ? Mono.just(true) : repository.addAllOrder(toSave))
                            .zipWith(toUpdate.isEmpty() ? Mono.just(true) : repository.updateAllOrder(toUpdate))
                            .then(Mono.just(true));
                });
    }

    //TODO название пересмотреть
    @Override
    @Transactional
    public Mono<Boolean> saveOrUpdateOrRemoveOrderItemsAll(@NonNull List<OrderItem> orderItems) {
        if (orderItems.isEmpty()) {
            return Mono.just(true);
        }

        var orderIds = extractOrderIds(orderItems);

        return getAllOrderItemsByOrderIds(orderIds)
                .flatMapMany(Flux::fromIterable)
                .collectMap(OrderItem::getId)
                .flatMap(existingMap -> {

                    var toSave = new ArrayList<OrderItem>();
                    var toUpdate = new ArrayList<OrderItem>();
                    var toDelete = new HashSet<Long>();

                    for (var actual : orderItems) {
                        var existing = existingMap.remove(actual.getId());

                        if (existing == null) {
                            toSave.add(actual);
                        } else {
                            if (existing.update(actual)) {
                                toUpdate.add(existing);
                            }
                        }
                    }

                    if (!existingMap.isEmpty()) {
                        toDelete.addAll(existingMap.keySet());
                    }

                    return (toSave.isEmpty() ? Mono.just(true) : repository.addAllOrderItem(toSave))
                            .zipWith(toUpdate.isEmpty() ? Mono.just(true) : repository.updateAllOrderItem(toUpdate))
                            .zipWith(toDelete.isEmpty() ? Mono.just(true) : repository.deleteAllOrderItemByIdIn(toDelete))
                            .then(Mono.just(true));
                });
    }

    @Override
    public Mono<List<OrderItem>> getAllOrderItemsByOrderIds(@NonNull Set<Long> ids) {
        if (ids.isEmpty()) {
            return Mono.just(List.of());
        }

        return repository.findAllOrderItemByOrderIds(ids)
                .collectList();
    }

    @Override
    public Mono<OrderSearchResult> searchOrders(OrderFilter filter, String cursor, Integer offset, Integer limit) {
        return repository.findAllOrderByFilter(filter, cursor, offset, limit);
    }

    private Set<Long> extractIds(List<Order> orders) {
        return orders.stream()
                .map(Order::getId)
                .collect(Collectors.toSet());
    }

    private Set<Long> extractOrderIds(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getOrderId)
                .collect(Collectors.toSet());
    }
}