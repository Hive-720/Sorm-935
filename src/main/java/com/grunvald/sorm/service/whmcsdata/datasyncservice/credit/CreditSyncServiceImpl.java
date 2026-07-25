package com.grunvald.sorm.service.whmcsdata.datasyncservice.credit;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.grunvald.sorm.service.whmcsdata.dataservice.credit.CreditService;
import com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.CreditMapper;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnBean(WhmcsProvider.class)
public class CreditSyncServiceImpl implements CreditSyncService {
    private final CreditService creditService;
    private final ClientService clientService;
    private final WhmcsProvider whmcs;
    private final CreditMapper creditMapper;


    @Override
    public Mono<CreditSyncPage> getActualData(Long clintCursorById, int limit, long applyDataLimit,
                                              OffsetDateTime historyDepthDate) {
        return clientService.getAllClient(clintCursorById, limit)
                .filter(idsResult -> !idsResult.clientIds().isEmpty())
                .flatMap(idsResult -> creditService.getLatestAllByClientId(new HashSet<>(idsResult.clientIds()))
                        .flatMapMany(Flux::fromIterable)
                        .collectMap(Credit::getClientId, Credit::getDate)
                        .flatMapMany(creditCursors -> Flux.fromIterable(idsResult.clientIds())
                                .concatMap(clientId -> {
                                    var minDate = getMinDate(creditCursors.get(clientId), historyDepthDate);
                                    return whmcs.getCredits(clientId, minDate)
                                            .map(creditDto -> creditMapper.map(creditDto, clientId));
                                })
                        )
                        .take(applyDataLimit + 1)
                        .collectList()
                        .map(list -> {
                                    var hasMoreByLimit = list.size() > applyDataLimit;
                                    var newCursor = getNewCursor(hasMoreByLimit, list, applyDataLimit,
                                            idsResult.clientIds());

                                    return CreditSyncPage.builder()
                                            .credits(list)
                                            .hasNextPage(hasMoreByLimit || idsResult.hasNextPage())
                                            .cursor(newCursor)
                                            .build();
                                }
                        ))
                .switchIfEmpty(Mono.just(CreditSyncPage.EMPTY));
    }

    private Long getNewCursor(boolean hasMoreByLimit, List<Credit> list, long applyDataLimit,
                              List<Long> clientsIds) {
        if (hasMoreByLimit) {
            var result = list.subList(0, (int) applyDataLimit);
            if (!result.isEmpty()) {
                var lastIncludedClientId = result.getLast().getClientId();
                var lastIdx = clientsIds.indexOf(lastIncludedClientId);
                return lastIdx >= 0 ? clientsIds.get(lastIdx) : clientsIds.getLast();
            }
        }

        return clientsIds.getLast();
    }

    private OffsetDateTime getMinDate(OffsetDateTime latestDate, OffsetDateTime historyDepthDate) {
        if (latestDate == null) {
            return historyDepthDate;
        }

        return historyDepthDate.isAfter(latestDate) ? historyDepthDate : latestDate;
    }

    @Override
    public Mono<Boolean> applyActualData(CreditSyncPage page) {
        return creditService.saveAll(page.getCredits());
    }
}