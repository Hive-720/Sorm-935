package com.kjsoft.sorm.service.whmcsdata.dataservice.contact;

import com.kjsoft.sorm.service.whmcsdata.dataservice.contact.dto.ContactSearchResult;
import com.kjsoft.sorm.service.whmcsdata.filter.ContactFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.contact.model.Contact;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {
    private final ContactRepository repository;


    @Override
    public Mono<List<Long>> getAllActiveIds() {
        return repository.getAllIds()
                .collectList();
    }

    @Override
    public Mono<List<Contact>> getAllByIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Mono.just(List.of());
        }

        return repository.findAllByIdIn(ids)
                .collectList();
    }

    @Override
    @Transactional
    public Mono<Boolean> saveOrUpdateAll(@NonNull List<Contact> incomingList) {
        if (incomingList.isEmpty()) {
            return Mono.just(true);
        }

        var ids = extractIds(incomingList);

        return repository.findAllByIdIn(ids)
                .collectMap(Contact::getId)
                .flatMap(existingMap -> {

                    var toSave = new ArrayList<Contact>();
                    var toUpdate = new ArrayList<Contact>();

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

    @Override
    public Mono<ContactSearchResult> search(ContactFilter filter, String cursor, Integer offset, Integer limit) {
        return repository.findAllByFilter(filter, cursor, offset, limit);
    }

    @Override
    public Mono<Boolean> deleteAll(@NonNull Set<Long> ids) {
        if (ids.isEmpty()) {
            return Mono.just(true);
        }

        return repository.softDeleteAll(ids);
    }

    private Set<Long> extractIds(List<Contact> clients) {
        return clients.stream()
                .map(Contact::getId)
                .collect(Collectors.toSet());
    }
}