package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.service.whmcsdata.dataservice.order.OrderService;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.OrderFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.OrderFilterPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.OrderResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.OrderResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.*;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class OrderResolver {
    private final OrderService orderService;
    private final OrderFilterPresentationMapper orderFilterMapper;
    private final OrderResultMapper orderResultMapper;


    @SchemaMapping(typeName = "QueryMessage")
    public Mono<OrderResult> orders(@Argument OrderFilterInput filter, @Argument Integer offset,
                                    @Argument Integer limit, @Argument String cursor) {
        var searchFilter = orderFilterMapper.map(filter);
        return orderService.searchOrders(searchFilter, cursor, offset, limit)
                .map(orderResultMapper::map);
    }

    @SchemaMapping(typeName = "Order", field = "items")
    public Mono<List<OrderItem>> getItemsForOrder(Order order, DataLoader<Long, List<OrderItem>> dataLoader) {
        return Mono.fromFuture(dataLoader.load(order.id()));
    }

    @SchemaMapping(typeName = "Order", field = "client")
    public Mono<Client> getClientForOrder(Order order, DataLoader<Long, Client> dataLoader) {
        return Mono.fromFuture(dataLoader.load(order.clientId()));
    }

    @SchemaMapping(typeName = "Order", field = "invoice")
    public Mono<Invoice> getInvoiceForOrder(Order order, DataLoader<Long, Invoice> dataLoader) {
        return Mono.fromFuture(dataLoader.load(order.invoiceId()));
    }

    @SchemaMapping(typeName = "Order", field = "contact")
    public Mono<Contact> getContactForOrder(Order order, DataLoader<Long, Contact> dataLoader) {
        return Mono.fromFuture(dataLoader.load(order.contactId()));
    }
}
