package com.kjsoft.sorm.service.whmcsdata.dataservice.client.dto;

import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.ClientProduct;

import java.util.List;


public record ClientProductSearchResult(String cursor, boolean hasNextPage, List<ClientProduct> clientProductList) {
}