package com.kjsoft.sorm.presentation.graphqlapi.operation.batchloader;

import com.kjsoft.sorm.service.whmcsdata.dataservice.contact.ContactService;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ContactPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Contact;
import lombok.RequiredArgsConstructor;
import org.dataloader.MappedBatchLoader;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;


@Component
@RequiredArgsConstructor
public class ContactBatchLoader implements MappedBatchLoader<Long, Contact> {
    private final ContactService service;
    private final ContactPresentationMapper mapper;


    @NonNull
    @Override
    public CompletionStage<Map<Long, Contact>> load(@NonNull Set<Long> contactIds) {
        return service.getAllByIds(contactIds)
                .flatMapMany(Flux::fromIterable)
                .map(mapper::map)
                .collectMap(Contact::id)
                .toFuture();
    }
}