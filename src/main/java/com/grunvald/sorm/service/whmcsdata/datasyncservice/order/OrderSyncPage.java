package com.grunvald.sorm.service.whmcsdata.datasyncservice.order;

import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.Order;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class OrderSyncPage {
    private List<Order> orders;
    private List<OrderItem> orderItems;
    private boolean hasNextPage;
    private Long nextOffset;
}