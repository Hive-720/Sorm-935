package com.kjsoft.sorm.service.whmcsdata.dataservice.client.dto;

import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.Client;

import java.util.List;


public record ClientSearchResult(String cursor, boolean hasNextPage, List<Client> clientList) {
}