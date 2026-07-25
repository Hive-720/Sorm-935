package com.grunvald.sorm.service.whmcsprovider.result;

import com.grunvald.sorm.service.whmcsprovider.dto.ProductProviderDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class ProductsResult {
    private List<ProductProviderDto> elements;
    private boolean hashNextPage;
}