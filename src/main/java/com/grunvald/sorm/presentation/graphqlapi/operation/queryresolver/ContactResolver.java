package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.service.whmcsdata.dataservice.contact.ContactService;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ContactFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.ContactFilterPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ContactResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ContactResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Contact;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;


@Controller
@RequiredArgsConstructor
public class ContactResolver {
    private final ContactService contactService;
    private final ContactFilterPresentationMapper contactFilterMapper;
    private final ContactResultMapper contactResultMapper;


    @SchemaMapping(typeName = "QueryMessage")
    public Mono<ContactResult> contacts(@Argument ContactFilterInput filter, @Argument Integer offset,
                                        @Argument Integer limit, @Argument String cursor) {
        var searchFilter = contactFilterMapper.map(filter);
        return contactService.search(searchFilter, cursor, offset, limit)
                .map(contactResultMapper::map);
    }

    @SchemaMapping(typeName = "Contact", field = "client")
    public Mono<Client> getClientForContact(Contact contact, DataLoader<Long, Client> dataLoader) {
        return Mono.fromFuture(dataLoader.load(contact.clientId()));
    }
}