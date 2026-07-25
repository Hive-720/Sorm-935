package com.grunvald.sorm.service.whmcsdata.datasyncservice.clientproduct;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.ClientProductMapper;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@ConditionalOnBean(WhmcsProvider.class)
public class ClientProductSyncServiceImpl implements ClientProductSyncService {
    private final ClientService clientService;
    private final WhmcsProvider whmcs;
    private final ClientProductMapper mapper;


    @Override
    public Mono<ClientProductSyncPage> getActualData(long offset, int limit) {
        return whmcs.getClientProducts(offset, limit)
                .flatMap(result -> Flux.fromIterable(result.getElements())
                        .map(mapper::map)
                        .collectList()
                        .map(list -> ClientProductSyncPage.builder()
                                .build()
                        )
                );
    }

    @Override
    public Mono<Boolean> applyActualData(ClientProductSyncPage page) {
        return clientService.saveOrUpdateAllClientProducts(page.getElements());
    }
}