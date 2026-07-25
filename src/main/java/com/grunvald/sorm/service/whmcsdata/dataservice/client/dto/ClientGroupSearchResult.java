package com.grunvald.sorm.service.whmcsdata.dataservice.client.dto;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup;

import java.util.List;


public record ClientGroupSearchResult(String cursor, boolean hasNextPage, List<ClientGroup> clientGroupList) {
}