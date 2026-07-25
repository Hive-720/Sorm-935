package com.grunvald.sorm.service.whmcsdata.datasyncservice.contact;

import com.grunvald.sorm.service.whmcsdata.dataservice.contact.ContactService;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.ContactMapper;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.ContactProviderDto;
import com.grunvald.sorm.service.whmcsprovider.result.ContactsResult;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;


@Service
@RequiredArgsConstructor
@ConditionalOnBean(WhmcsProvider.class)
public class ContactSyncServiceImpl implements ContactSyncService {
    private final ContactService contactService;
    private final WhmcsProvider whmcs;
    private final ContactMapper contactMapper;


    @Override
    public Mono<ContactSyncPage> getActualData(long offset, int limit) {
        return whmcs.getContacts(offset, limit)
                .flatMap(result -> Flux.fromIterable(result.getElements())
                        .map(contactMapper::map)
                        .collectList()
                        .map(list -> ContactSyncPage.builder()
                                .elements(list)
                                .hasNextPage(result.isHasNextPage())
                                .nextOffset(result.getNextOffset())
                                .build()
                        )
                );
    }

    @Override
    public Mono<Boolean> applyActualData(ContactSyncPage page) {
        return contactService.saveOrUpdateAll(page.getElements());
    }

    @Override
    public Mono<Boolean> delete(long initialOffset, int limit) {
        return contactService.getAllActiveIds()
                .filter(localActiveIds -> !localActiveIds.isEmpty())
                .flatMap(localActiveIds -> {
                    var remainingContactIds = new HashSet<>(localActiveIds);
                    return whmcs.getContacts(initialOffset, limit)
                            .expand(result -> {
                                if (!result.isHasNextPage()) {
                                    return Mono.empty();
                                }

                                return whmcs.getContacts(result.getNextOffset(), limit);
                            })
                            .doOnNext(result -> reduceRemaining(result, remainingContactIds))
                            .then(Mono.just(remainingContactIds));
                })
                .filter(removed -> !removed.isEmpty())
                .flatMap(contactService::deleteAll)
                .switchIfEmpty(Mono.just(true));
    }

    private void reduceRemaining(ContactsResult result, HashSet<Long> remainingContactIds) {
        result.getElements().stream()
                .map(ContactProviderDto::getId)
                .forEach(remainingContactIds::remove);
    }
}