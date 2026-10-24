package com.grunvald.sorm.service.whmcsdata.datasyncservice.order;

import com.grunvald.sorm.service.whmcsdata.dataservice.order.OrderService;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.Order;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.OrderProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.OrderItemMapper;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.OrderMapper;
import com.grunvald.sorm.service.whmcsprovider.result.OrdersResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderSyncServiceImplTest {

    private static final OffsetDateTime HISTORY_DEPTH = OffsetDateTime.parse("2026-01-01T00:00:00Z");

    @Mock
    private OrderService orderService;
    @Mock
    private WhmcsProvider whmcs;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemMapper orderItemMapper;

    private OrderSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OrderSyncServiceImpl(orderService, whmcs, orderMapper, orderItemMapper);
    }

    private OrderProviderDto.LineItem lineItem(long relId) {
        var item = new OrderProviderDto.LineItem();
        item.setRelId(relId);
        return item;
    }

    private OrderProviderDto orderDto(long id, List<OrderProviderDto.LineItem> items) {
        var dto = new OrderProviderDto();
        dto.setId(id);
        dto.setLineItems(items);
        return dto;
    }

    @Test
    void getActualData_mapsOrdersAndLineItems() {
        var itemDto = lineItem(500L);
        var orderDto = orderDto(100L, List.of(itemDto));
        var mappedOrder = new Order();
        mappedOrder.setId(100L);
        var mappedItem = new OrderItem();
        mappedItem.setRelId(500L);

        when(whmcs.getOrders(0L, 10, HISTORY_DEPTH))
                .thenReturn(Mono.just(OrdersResult.builder().elements(List.of(orderDto)).hasNextPage(false).nextOffset(0L).build()));
        when(orderMapper.map(orderDto)).thenReturn(mappedOrder);
        when(orderItemMapper.map(itemDto, 100L)).thenReturn(mappedItem);

        StepVerifier.create(service.getActualData(0L, 10, HISTORY_DEPTH))
                .assertNext(page -> {
                    assertEquals(List.of(mappedOrder), page.getOrders());
                    assertEquals(List.of(mappedItem), page.getOrderItems());
                    assertFalse(page.isHasNextPage());
                })
                .verifyComplete();
    }

    @Test
    void getActualData_orderWithoutLineItems_mapsEmptyItemList() {
        var orderDto = orderDto(100L, List.of());
        var mappedOrder = new Order();
        mappedOrder.setId(100L);

        when(whmcs.getOrders(0L, 10, HISTORY_DEPTH))
                .thenReturn(Mono.just(OrdersResult.builder().elements(List.of(orderDto)).hasNextPage(true).nextOffset(10L).build()));
        when(orderMapper.map(orderDto)).thenReturn(mappedOrder);

        StepVerifier.create(service.getActualData(0L, 10, HISTORY_DEPTH))
                .assertNext(page -> {
                    assertEquals(1, page.getOrders().size());
                    assertTrue(page.getOrderItems().isEmpty());
                    assertTrue(page.isHasNextPage());
                    assertEquals(10L, page.getNextOffset());
                })
                .verifyComplete();
    }

    @Test
    void applyActualData_savesOrdersThenOrderItems() {
        var page = OrderSyncPage.builder()
                .orders(List.of(new Order()))
                .orderItems(List.of(new OrderItem()))
                .build();
        when(orderService.saveOrUpdateOrderAll(page.getOrders())).thenReturn(Mono.just(true));
        when(orderService.saveOrUpdateOrRemoveOrderItemsAll(page.getOrderItems())).thenReturn(Mono.just(true));

        StepVerifier.create(service.applyActualData(page))
                .expectNext(true)
                .verifyComplete();

        verify(orderService).saveOrUpdateOrderAll(page.getOrders());
        verify(orderService).saveOrUpdateOrRemoveOrderItemsAll(page.getOrderItems());
    }
}
