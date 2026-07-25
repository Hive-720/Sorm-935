package com.grunvald.sorm.service.whmcsdata.dataservice.affiliate;

import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.dto.AffiliateSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.AffiliateFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AffiliateServiceImpl implements AffiliateService {
    private final AffiliateRepository repository;


    @Override
    public Mono<AffiliateSearchResult> search(AffiliateFilter filter, String cursor, Integer offset, Integer limit) {
        return repository.findAllAffiliateByFilter(filter, cursor, offset, limit);
    }

    @Override
    public Mono<List<Affiliate>> getAllByIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Mono.just(List.of());
        }

        return repository.findAllByIdIn(ids)
                .collectList();
    }

    @Override
    @Transactional
    public Mono<Boolean> saveOrUpdateAll(List<Affiliate> incomingList) {
        if (incomingList.isEmpty()) {
            return Mono.just(true);
        }

        var ids = extractIds(incomingList);

        return repository.findAllByIdIn(ids)
                .collectMap(Affiliate::getId)
                .flatMap(existingMap -> {

                    var toSave = new ArrayList<Affiliate>();
                    var toUpdate = new ArrayList<Affiliate>();

                    for (var actual : incomingList) {
                        var local = existingMap.get(actual.getId());

                        if (local == null) {
                            toSave.add(actual);
                        } else {
                            if (local.update(actual)) {
                                toUpdate.add(local);
                            }
                        }
                    }

                    return (toSave.isEmpty() ? Mono.just(true) : repository.addAll(toSave))
                            .zipWith(toUpdate.isEmpty() ? Mono.just(true) : repository.updateAll(toUpdate))
                            .then(Mono.just(true));
                });
    }

    private Set<Long> extractIds(List<Affiliate> affiliates) {
        return affiliates.stream()
                .map(Affiliate::getId)
                .collect(Collectors.toSet());
    }
}