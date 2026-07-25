package com.grunvald.sorm.service.whmcsdata.datasyncservice.client;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class ClientSyncPage {
    private List<Client> elements;
    private boolean hasNextPage;
    private Long nextOffset;
}