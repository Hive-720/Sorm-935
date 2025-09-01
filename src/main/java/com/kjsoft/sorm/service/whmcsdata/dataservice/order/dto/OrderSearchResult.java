package com.kjsoft.sorm.service.whmcsdata.dataservice.order.dto;

import com.kjsoft.sorm.service.whmcsdata.dataservice.order.model.Order;

import java.util.List;


public record OrderSearchResult(String cursor, boolean hasNextPage, List<Order> orderList) {
}