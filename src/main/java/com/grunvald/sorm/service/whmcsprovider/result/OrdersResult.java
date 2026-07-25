package com.grunvald.sorm.service.whmcsprovider.result;

import com.grunvald.sorm.service.whmcsprovider.dto.OrderProviderDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class OrdersResult {
    private List<OrderProviderDto> elements;
    private boolean hasNextPage;
    private long nextOffset;
}