package com.grunvald.sorm.service.whmcsdata.datasyncservice.clientgroup;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup;
import reactor.core.publisher.Mono;

import java.util.List;


public interface ClientGroupSyncService {

    Mono<List<ClientGroup>> getActualData();

    Mono<Boolean> applyActualData(List<ClientGroup> data);
}