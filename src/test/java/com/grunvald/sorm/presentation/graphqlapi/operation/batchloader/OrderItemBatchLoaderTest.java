package com.grunvald.sorm.presentation.graphqlapi.operation.batchloader;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.OrderItem;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.OrderItemPresentationMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderItemBatchLoaderTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderItemPresentationMapper mapper;

    private OrderItemBatchLoader loader;

    @BeforeEach
    void setUp() {
        loader = new OrderItemBatchLoader(orderService, mapper);
    }

    private static com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem model(Long orderId) {
        var m = new com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem();
        m.setOrderId(orderId);
        return m;
    }

    private static OrderItem presentation(Long orderId, String name) {
        return new OrderItem(orderId, null, null, null, name, null, null, null, null);
    }

    @Test
    void load_groupsItemsByOrderIdPreservingOrderAndMapsEachItem() {
        var orderIds = Set.of(10L, 20L);
        var item10a = model(10L);
        var item20 = model(20L);
        var item10b = model(10L);
        when(orderService.getAllOrderItemsByOrderIds(orderIds)).thenReturn(Mono.just(List.of(item10a, item20, item10b)));
        when(mapper.map(item10a)).thenReturn(presentation(10L, "a"));
        when(mapper.map(item20)).thenReturn(presentation(20L, "b"));
        when(mapper.map(item10b)).thenReturn(presentation(10L, "c"));

        Map<Long, List<OrderItem>> result = loader.load(orderIds).toCompletableFuture().join();

        assertEquals(Set.of(10L, 20L), result.keySet());
        assertEquals(List.of(presentation(10L, "a"), presentation(10L, "c")), result.get(10L));
        assertEquals(List.of(presentation(20L, "b")), result.get(20L));
        verify(orderService, times(1)).getAllOrderItemsByOrderIds(orderIds);
        verify(mapper).map(item10a);
        verify(mapper).map(item20);
        verify(mapper).map(item10b);
    }

    @Test
    void load_emptyIds_returnsEmptyMapAndStillDelegatesToService() {
        when(orderService.getAllOrderItemsByOrderIds(Set.of())).thenReturn(Mono.just(List.of()));

        Map<Long, List<OrderItem>> result = loader.load(Set.of()).toCompletableFuture().join();

        assertTrue(result.isEmpty());
        verify(orderService).getAllOrderItemsByOrderIds(Set.of());
        verifyNoInteractions(mapper);
    }

    @Test
    void load_serviceReturnsEmptyList_returnsEmptyMapWithoutMapping() {
        when(orderService.getAllOrderItemsByOrderIds(Set.of(1L, 2L))).thenReturn(Mono.just(List.of()));

        Map<Long, List<OrderItem>> result = loader.load(Set.of(1L, 2L)).toCompletableFuture().join();

        assertTrue(result.isEmpty());
        verifyNoInteractions(mapper);
    }

    @Test
    void load_serviceError_propagatesAsFailedFuture() {
        var boom = new IllegalStateException("db down");
        when(orderService.getAllOrderItemsByOrderIds(Set.of(1L))).thenReturn(Mono.error(boom));

        CompletionException thrown = assertThrows(CompletionException.class,
                () -> loader.load(Set.of(1L)).toCompletableFuture().join());

        assertSame(boom, thrown.getCause());
        verifyNoInteractions(mapper);
    }
}
