package com.grunvald.sorm.service.whmcsdata.dataservice.product.dto;

import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product;

import java.util.List;


public record ProductSearchResult(String cursor, boolean hasNextPage, List<Product> productList) {
}