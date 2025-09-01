package com.kjsoft.sorm.service.whmcsdata.dataservice.contact;

import com.kjsoft.sorm.service.whmcsdata.dataservice.contact.dto.ContactSearchResult;
import com.kjsoft.sorm.service.whmcsdata.filter.ContactFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.contact.model.Contact;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface ContactRepository {

    Flux<Long> getAllIds();

    Mono<Boolean> addAll(List<Contact> contacts);

    Mono<Boolean> updateAll(List<Contact> contacts);

    Mono<Boolean> softDeleteAll(Set<Long> ids);

    Flux<Contact> findAllByIdIn(Set<Long> ids);

    Mono<ContactSearchResult> findAllByFilter(ContactFilter filter, String cursor, Integer offset, Integer limit);
}