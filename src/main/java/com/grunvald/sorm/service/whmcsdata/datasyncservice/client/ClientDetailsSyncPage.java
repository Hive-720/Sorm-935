package com.grunvald.sorm.service.whmcsdata.datasyncservice.client;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;


@Getter
@Builder
public class ClientDetailsSyncPage {

    public static final ClientDetailsSyncPage EMPTY = ClientDetailsSyncPage.builder()
            .actual(List.of())
            .lastLogins(List.of())
            .removedClients(Set.of())
            .hasNextPage(false)
            .cursor(null)
            .build();

    private List<Client> actual;
    private List<ClientLogin> lastLogins;
    private Set<Long> removedClients;
    private boolean hasNextPage;
    private Long cursor;
}