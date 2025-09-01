package com.kjsoft.sorm.service.whmcsdata.datasyncservice.clientgroup;

import com.kjsoft.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.ClientGroup;
import com.kjsoft.sorm.service.whmcsprovider.dto.mapper.ClientGroupMapper;
import com.kjsoft.sorm.service.whmcsprovider.WhmcsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
@RequiredArgsConstructor
@ConditionalOnBean(WhmcsProvider.class)
public class ClientGroupSyncServiceImpl implements ClientGroupSyncService {
    private final ClientService clientService;
    private final WhmcsProvider whmcs;
    private final ClientGroupMapper mapper;


    @Override
    public Mono<List<ClientGroup>> getActualData() {
        return whmcs.getClientGroups()
                .map(mapper::map)
                .collectList();
    }

    @Override
    public Mono<Boolean> applyActualData(List<ClientGroup> data) {
        return clientService.saveOrUpdateAllClientGroup(data);
    }
}