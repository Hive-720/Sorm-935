package com.grunvald.sorm.service.whmcsdata.dataservice.contact;

import com.grunvald.sorm.service.whmcsdata.dataservice.contact.dto.ContactSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.ContactFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface ContactService {

    Mono<List<Long>> getAllActiveIds();

    Mono<List<Contact>> getAllByIds(Set<Long> ids);

    Mono<Boolean> saveOrUpdateAll(List<Contact> contactList);

    Mono<ContactSearchResult> search(ContactFilter filter, String cursor, Integer offset, Integer limit);

    Mono<Boolean> deleteAll(Set<Long> ids);
}