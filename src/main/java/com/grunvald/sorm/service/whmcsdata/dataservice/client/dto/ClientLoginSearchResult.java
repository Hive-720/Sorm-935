package com.grunvald.sorm.service.whmcsdata.dataservice.client.dto;


import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin;

import java.util.List;


public record ClientLoginSearchResult(String cursor, boolean hasNextPage, List<ClientLogin> clientLoginList) {
}