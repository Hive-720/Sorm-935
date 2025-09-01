package com.kjsoft.sorm.infrastructure.persistence.data.order;

import com.kjsoft.sorm.service.whmcsdata.dataservice.order.OrderRepository;
import com.kjsoft.sorm.service.whmcsdata.filter.OrderFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.order.model.Order;
import com.kjsoft.sorm.service.whmcsdata.dataservice.order.model.OrderItem;
import com.kjsoft.sorm.service.whmcsdata.dataservice.order.dto.OrderSearchResult;
import com.kjsoft.sorm.infrastructure.persistence.data.order.entity.OrderDb;
import com.kjsoft.sorm.infrastructure.persistence.data.order.entity.OrderItemDb;
import com.kjsoft.sorm.infrastructure.persistence.data.order.entity.mapper.OrderItemPersistenceMapper;
import com.kjsoft.sorm.infrastructure.persistence.data.order.entity.mapper.OrderPersistenceMapper;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.order.mapper.OrderFilterPersistenceMapper;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kjsoft.sorm.infrastructure.persistence.util.PersistenceHelper.bindOrNull;


@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final DatabaseClient databaseClient;
    private final DatabaseClientAdapter databaseClientAdapter;
    private final OrderPersistenceMapper orderMapper;
    private final OrderItemPersistenceMapper orderItemMapper;
    private final OrderFilterPersistenceMapper orderFilterMapper;


    @Override
    @Transactional
    public Mono<Boolean> addAllOrder(List<Order> orders) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement(""" 
                    INSERT INTO orders
                    (id, client_id, contact_id, invoice_id, amount, currency, status, ip_address,
                     payment_method, payment_method_name, payment_status, date)
                    VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12)
                    """);

            return Flux.fromIterable(orders)
                    .map(orderMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getId());
                        stmt.bind(1, entity.getClientId());
                        bindOrNull(stmt, 2, entity.getContactId(), Long.class);
                        bindOrNull(stmt, 3, entity.getInvoiceId(), Long.class);
                        bindOrNull(stmt, 4, entity.getAmount(), BigDecimal.class);
                        bindOrNull(stmt, 5, entity.getCurrency(), String.class);
                        bindOrNull(stmt, 6, entity.getStatus(), String.class);
                        bindOrNull(stmt, 7, entity.getIpAddress(), String.class);
                        bindOrNull(stmt, 8, entity.getPaymentMethod(), String.class);
                        bindOrNull(stmt, 9, entity.getPaymentMethodName(), String.class);
                        bindOrNull(stmt, 10, entity.getPaymentStatus(), String.class);
                        bindOrNull(stmt, 11, entity.getDate(), OffsetDateTime.class);

                        if (index < orders.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == orders.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> updateAllOrder(List<Order> orders) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    UPDATE orders
                    SET amount = $1,
                        currency = $2,
                        status = $3,
                        ip_address = $4,
                        payment_method = $5,
                        payment_method_name = $6,
                        payment_status = $7
                    WHERE id = $8
                    """);

            return Flux.fromIterable(orders)
                    .map(orderMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        bindOrNull(stmt, 0, entity.getAmount(), BigDecimal.class);
                        bindOrNull(stmt, 1, entity.getCurrency(), String.class);
                        bindOrNull(stmt, 2, entity.getStatus(), String.class);
                        bindOrNull(stmt, 3, entity.getIpAddress(), String.class);
                        bindOrNull(stmt, 4, entity.getPaymentMethod(), String.class);
                        bindOrNull(stmt, 5, entity.getPaymentMethodName(), String.class);
                        bindOrNull(stmt, 6, entity.getPaymentStatus(), String.class);
                        stmt.bind(7, entity.getId());

                        if (index < orders.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == orders.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> addAllOrderItem(List<OrderItem> orderItems) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement(""" 
                    INSERT INTO order_items
                    (id, order_id, rel_id, type, product_type, product_name, domain, billing_cycle,
                     amount, status)
                    VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)
                    """);

            return Flux.fromIterable(orderItems)
                    .map(orderItemMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        bindOrNull(stmt, 0, entity.getId(), Long.class);
                        bindOrNull(stmt, 1, entity.getOrderId(), Long.class);
                        bindOrNull(stmt, 2, entity.getRelId(), Long.class);
                        bindOrNull(stmt, 3, entity.getType(), String.class);
                        bindOrNull(stmt, 4, entity.getProductType(), String.class);
                        bindOrNull(stmt, 5, entity.getProductName(), String.class);
                        bindOrNull(stmt, 6, entity.getDomain(), String.class);
                        bindOrNull(stmt, 7, entity.getBillingCycle(), String[].class);
                        bindOrNull(stmt, 8, entity.getAmount(), BigDecimal.class);
                        bindOrNull(stmt, 9, entity.getStatus(), String.class);

                        if (index < orderItems.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == orderItems.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> updateAllOrderItem(List<OrderItem> orderItems) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    UPDATE order_items
                    SET type = $1,
                        product_type = $2,
                        product_name = $3,
                        domain = $4,
                        billing_cycle = $5,
                        amount = $6,
                        status = $7
                    WHERE id = $8
                    """);

            return Flux.fromIterable(orderItems)
                    .map(orderItemMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        bindOrNull(stmt, 0, entity.getType(), String.class);
                        bindOrNull(stmt, 1, entity.getProductType(), String.class);
                        bindOrNull(stmt, 2, entity.getProductName(), String.class);
                        bindOrNull(stmt, 3, entity.getDomain(), String.class);
                        bindOrNull(stmt, 4, entity.getBillingCycle(), String[].class);
                        bindOrNull(stmt, 5, entity.getAmount(), BigDecimal.class);
                        bindOrNull(stmt, 6, entity.getStatus(), String.class);
                        stmt.bind(7, entity.getId());

                        if (index < orderItems.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == orderItems.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> deleteAllOrderItemByIdIn(Set<Long> ids) {
        return databaseClient.sql("DELETE FROM order_items WHERE id IN (:ids)")
                .bind("ids", ids)
                .fetch()
                .rowsUpdated()
                .map(rows -> rows > 0)
                .defaultIfEmpty(false);
    }

    @Override
    @Transactional
    public Flux<Order> findAllOrderByIdIn(Set<Long> ids) {
        return databaseClient.sql("SELECT * FROM orders WHERE id IN(:ids)")
                .bind("ids", ids)
                .mapProperties(OrderDb.class)
                .all()
                .map(orderMapper::map);
    }

    @Override
    public Flux<OrderItem> findAllOrderItemByOrderIds(Set<Long> orderIds) {
        return databaseClient.sql("SELECT * FROM order_items WHERE order_id IN (:ids)")
                .bind("ids", orderIds)
                .mapProperties(OrderItemDb.class)
                .all()
                .map(orderItemMapper::map);
    }

    @Override
    public Mono<OrderSearchResult> findAllOrderByFilter(OrderFilter filter, String cursor, Integer offset, Integer limit) {
        var dbFilter = orderFilterMapper.map(filter);
        var limitPlusOne = limit + 1;

        return databaseClientAdapter.all(dbFilter, cursor, offset, limitPlusOne, OrderDb.class)
                .map(orderMapper::map)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    var hasNextPage = list.size() > limit;
                    var resultList = hasNextPage ? list.subList(0, limit) : list;
                    var lastUser = resultList.getLast();
                    var nextCursor = lastUser.getId().toString();
                    return new OrderSearchResult(nextCursor, hasNextPage, resultList);
                })
                .switchIfEmpty(Mono.just(new OrderSearchResult(null, false, List.of())));
    }
}