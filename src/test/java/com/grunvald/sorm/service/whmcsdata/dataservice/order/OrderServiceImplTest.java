package com.grunvald.sorm.service.whmcsdata.dataservice.order;

import com.grunvald.sorm.service.whmcsdata.dataservice.order.dto.OrderSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.Order;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem;
import com.grunvald.sorm.service.whmcsdata.filter.OrderFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository repository;

    @Captor
    private ArgumentCaptor<List<Order>> orderListCaptor;
    @Captor
    private ArgumentCaptor<List<OrderItem>> itemListCaptor;
    @Captor
    private ArgumentCaptor<Set<Long>> idSetCaptor;

    private OrderServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OrderServiceImpl(repository);
    }

    private Order order(long id, String status) {
        var order = new Order();
        order.setId(id);
        order.setClientId(100L);
        order.setStatus(status);
        order.setAmount(new BigDecimal("9.99"));
        order.setCurrency("USD");
        return order;
    }

    private OrderItem item(long id, long orderId, String status) {
        var item = new OrderItem();
        item.setId(id);
        item.setOrderId(orderId);
        item.setType("type");
        item.setProductName("product-" + id);
        item.setAmount(new BigDecimal("4.99"));
        item.setStatus(status);
        return item;
    }

    // ---------- getAllOrderByIds ----------

    @Test
    void getAllOrderByIds_emptyIds_returnsEmptyWithoutRepositoryCall() {
        StepVerifier.create(service.getAllOrderByIds(Set.of()))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();

        verify(repository, never()).findAllOrderByIdIn(anySet());
    }

    @Test
    void getAllOrderByIds_returnsOrders() {
        var order = order(1, "Active");
        when(repository.findAllOrderByIdIn(Set.of(1L))).thenReturn(Flux.just(order));

        StepVerifier.create(service.getAllOrderByIds(Set.of(1L)))
                .expectNext(List.of(order))
                .verifyComplete();
    }

    // ---------- saveOrUpdateOrderAll ----------

    @Test
    void saveOrUpdateOrderAll_emptyList_returnsTrue() {
        StepVerifier.create(service.saveOrUpdateOrderAll(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).findAllOrderByIdIn(anySet());
    }

    @Test
    void saveOrUpdateOrderAll_allNew_addsWithoutUpdate() {
        when(repository.findAllOrderByIdIn(Set.of(1L, 2L))).thenReturn(Flux.empty());
        when(repository.addAllOrder(anyList())).thenReturn(Mono.just(true));

        var order1 = order(1, "Active");
        var order2 = order(2, "Pending");

        StepVerifier.create(service.saveOrUpdateOrderAll(List.of(order1, order2)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAllOrder(List.of(order1, order2));
        verify(repository, never()).updateAllOrder(anyList());
    }

    @Test
    void saveOrUpdateOrderAll_unchanged_skipsPersistence() {
        var existing = order(1, "Active");
        when(repository.findAllOrderByIdIn(Set.of(1L))).thenReturn(Flux.just(existing));

        StepVerifier.create(service.saveOrUpdateOrderAll(List.of(order(1, "Active"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAllOrder(anyList());
        verify(repository, never()).updateAllOrder(anyList());
    }

    @Test
    void saveOrUpdateOrderAll_changed_updatesExisting() {
        var existing = order(1, "Active");
        when(repository.findAllOrderByIdIn(Set.of(1L))).thenReturn(Flux.just(existing));
        when(repository.updateAllOrder(anyList())).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveOrUpdateOrderAll(List.of(order(1, "Cancelled"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository).updateAllOrder(orderListCaptor.capture());
        assertEquals(1, orderListCaptor.getValue().size());
        assertEquals("Cancelled", orderListCaptor.getValue().getFirst().getStatus());
        verify(repository, never()).addAllOrder(anyList());
    }

    @Test
    void saveOrUpdateOrderAll_mixed_savesAndUpdates() {
        var existing = order(1, "Active");
        when(repository.findAllOrderByIdIn(Set.of(1L, 2L))).thenReturn(Flux.just(existing));
        when(repository.addAllOrder(anyList())).thenReturn(Mono.just(true));
        when(repository.updateAllOrder(anyList())).thenReturn(Mono.just(true));

        var changed = order(1, "Cancelled");
        var fresh = order(2, "Pending");

        StepVerifier.create(service.saveOrUpdateOrderAll(List.of(changed, fresh)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAllOrder(List.of(fresh));
        verify(repository).updateAllOrder(List.of(existing));
    }

    // ---------- getAllOrderItemsByOrderIds ----------

    @Test
    void getAllOrderItemsByOrderIds_emptyIds_returnsEmpty() {
        StepVerifier.create(service.getAllOrderItemsByOrderIds(Set.of()))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();

        verify(repository, never()).findAllOrderItemByOrderIds(anySet());
    }

    @Test
    void getAllOrderItemsByOrderIds_returnsItems() {
        var item = item(1, 10, "Active");
        when(repository.findAllOrderItemByOrderIds(Set.of(10L))).thenReturn(Flux.just(item));

        StepVerifier.create(service.getAllOrderItemsByOrderIds(Set.of(10L)))
                .expectNext(List.of(item))
                .verifyComplete();
    }

    // ---------- saveOrUpdateOrRemoveOrderItemsAll ----------

    @Test
    void saveOrUpdateOrRemoveOrderItemsAll_emptyList_returnsTrue() {
        StepVerifier.create(service.saveOrUpdateOrRemoveOrderItemsAll(List.of()))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).findAllOrderItemByOrderIds(anySet());
    }

    @Test
    void saveOrUpdateOrRemoveOrderItemsAll_allNew_addsOnly() {
        when(repository.findAllOrderItemByOrderIds(Set.of(10L))).thenReturn(Flux.empty());
        when(repository.addAllOrderItem(anyList())).thenReturn(Mono.just(true));

        var item1 = item(1, 10, "Active");
        var item2 = item(2, 10, "Active");

        StepVerifier.create(service.saveOrUpdateOrRemoveOrderItemsAll(List.of(item1, item2)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAllOrderItem(List.of(item1, item2));
        verify(repository, never()).updateAllOrderItem(anyList());
        verify(repository, never()).deleteAllOrderItemByIdIn(anySet());
    }

    @Test
    void saveOrUpdateOrRemoveOrderItemsAll_addsUpdatesAndDeletes() {
        var existingChanged = item(1, 10, "Active");
        var existingStale = item(2, 10, "Active");
        when(repository.findAllOrderItemByOrderIds(Set.of(10L)))
                .thenReturn(Flux.just(existingChanged, existingStale));
        when(repository.addAllOrderItem(anyList())).thenReturn(Mono.just(true));
        when(repository.updateAllOrderItem(anyList())).thenReturn(Mono.just(true));
        when(repository.deleteAllOrderItemByIdIn(anySet())).thenReturn(Mono.just(true));

        var changed = item(1, 10, "Cancelled");
        var fresh = item(3, 10, "Active");

        StepVerifier.create(service.saveOrUpdateOrRemoveOrderItemsAll(List.of(changed, fresh)))
                .expectNext(true)
                .verifyComplete();

        verify(repository).addAllOrderItem(List.of(fresh));
        verify(repository).updateAllOrderItem(List.of(existingChanged));
        verify(repository).deleteAllOrderItemByIdIn(idSetCaptor.capture());
        assertEquals(Set.of(2L), idSetCaptor.getValue());
    }

    @Test
    void saveOrUpdateOrRemoveOrderItemsAll_unchanged_callsNothing() {
        var existing = item(1, 10, "Active");
        when(repository.findAllOrderItemByOrderIds(Set.of(10L))).thenReturn(Flux.just(existing));

        StepVerifier.create(service.saveOrUpdateOrRemoveOrderItemsAll(List.of(item(1, 10, "Active"))))
                .expectNext(true)
                .verifyComplete();

        verify(repository, never()).addAllOrderItem(anyList());
        verify(repository, never()).updateAllOrderItem(anyList());
        verify(repository, never()).deleteAllOrderItemByIdIn(anySet());
    }

    // ---------- searchOrders ----------

    @Test
    void searchOrders_delegatesToRepository() {
        var filter = new OrderFilter();
        var expected = new OrderSearchResult(null, false, List.of());
        when(repository.findAllOrderByFilter(filter, "2", 0, 25)).thenReturn(Mono.just(expected));

        StepVerifier.create(service.searchOrders(filter, "2", 0, 25))
                .expectNext(expected)
                .verifyComplete();
    }
}
