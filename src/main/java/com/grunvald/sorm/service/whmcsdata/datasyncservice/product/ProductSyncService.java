package com.grunvald.sorm.service.whmcsdata.datasyncservice.product;

import reactor.core.publisher.Mono;


public interface ProductSyncService {

    Mono<ProductSync> getActualProducts();

    Mono<Boolean> applyActualData(ProductSync data);
}