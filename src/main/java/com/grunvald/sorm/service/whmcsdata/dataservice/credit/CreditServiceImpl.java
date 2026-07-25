package com.grunvald.sorm.service.whmcsdata.dataservice.credit;

import com.grunvald.sorm.service.whmcsdata.filter.CreditFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit;
import com.grunvald.sorm.service.whmcsdata.dataservice.credit.dto.CreditSearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {
    private final CreditRepository repository;


    @Override
    public Mono<Boolean> saveAll(@NonNull List<Credit> credits) {
        if (credits.isEmpty()) {
            return Mono.just(true);
        }

        return repository.addAll(credits);
    }

    @Override
    public Mono<List<Credit>> getLatestAllByClientId(@NonNull Set<Long> clientId) {
        if (clientId.isEmpty()) {
            return Mono.just(List.of());
        }

        return repository.findLatestAllByClientId(clientId)
                .collectList();
    }

    @Override
    public Mono<CreditSearchResult> search(CreditFilter filter, String cursor, Integer offset, Integer limit) {
        return repository.findAllByFilter(filter, cursor, offset, limit);
    }
}