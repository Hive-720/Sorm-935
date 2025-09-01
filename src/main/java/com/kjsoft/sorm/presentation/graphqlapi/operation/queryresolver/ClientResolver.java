package com.kjsoft.sorm.presentation.graphqlapi.operation.queryresolver;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientLoginFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.ClientLoginFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ClientLoginResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ClientLoginResultMapper;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientGroupFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientProductFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.ClientFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.ClientGroupFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.ClientProductFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ClientGroupResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ClientProductResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ClientResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ClientGroupResultMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ClientProductResultMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ClientResultMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.*;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;


@Controller
@RequiredArgsConstructor
public class ClientResolver {
    private final ClientService clientService;
    private final ClientFilterPresentationMapper clientFilterMapper;
    private final ClientProductFilterPresentationMapper clientProductFilterMapper;
    private final ClientGroupFilterPresentationMapper clientGroupFilterMapper;
    private final ClientLoginFilterPresentationMapper clientLoginFilterMapper;
    private final ClientResultMapper clientResultMapper;
    private final ClientProductResultMapper clientProductResultMapper;
    private final ClientGroupResultMapper clientGroupResultMapper;
    private final ClientLoginResultMapper clientLoginResultMapper;


    @SchemaMapping(typeName = "QueryMessage")
    public Mono<ClientResult> clients(@Argument ClientFilterInput filter, @Argument Integer offset,
                                      @Argument Integer limit, @Argument String cursor) {
        var searchFilter = clientFilterMapper.map(filter);
        return clientService.searchClients(searchFilter, cursor, offset, limit)
                .map(clientResultMapper::map);
    }

    @SchemaMapping(typeName = "QueryMessage")
    public Mono<ClientProductResult> clientProducts(@Argument ClientProductFilterInput filter, @Argument Integer offset,
                                                    @Argument Integer limit, @Argument String cursor) {
        var searchFilter = clientProductFilterMapper.map(filter);
        return clientService.searchClientProducts(searchFilter, cursor, offset, limit)
                .map(clientProductResultMapper::map);
    }

    @SchemaMapping(typeName = "QueryMessage")
    public Mono<ClientGroupResult> clientGroups(@Argument ClientGroupFilterInput filter, @Argument Integer offset,
                                                @Argument Integer limit, @Argument String cursor) {
        var searchFilter = clientGroupFilterMapper.map(filter);
        return clientService.searchClientGroups(searchFilter, cursor, offset, limit)
                .map(clientGroupResultMapper::map);
    }

    @SchemaMapping(typeName = "QueryMessage")
    public Mono<ClientLoginResult> clientLogins(@Argument ClientLoginFilterInput filter, @Argument Integer offset,
                                                @Argument Integer limit, @Argument String cursor) {
        var searchFilter = clientLoginFilterMapper.map(filter);
        return clientService.searchClientLogins(searchFilter, cursor, offset, limit)
                .map(clientLoginResultMapper::map);
    }

    @SchemaMapping(typeName = "ClientProduct", field = "product")
    public Mono<Product> getProductForClientProduct(Contact contact, DataLoader<Long, Product> dataLoader) {
        return Mono.fromFuture(dataLoader.load(contact.clientId()));
    }

    @SchemaMapping(typeName = "ClientProduct", field = "order")
    public Mono<Order> getOrderForClientProduct(Contact contact, DataLoader<Long, Order> dataLoader) {
        return Mono.fromFuture(dataLoader.load(contact.clientId()));
    }

    @SchemaMapping(typeName = "ClientProduct", field = "client")
    public Mono<Client> getClientForClientProduct(Contact contact, DataLoader<Long, Client> dataLoader) {
        return Mono.fromFuture(dataLoader.load(contact.clientId()));
    }

    @SchemaMapping(typeName = "Client", field = "userOwner")
    public Mono<User> getUserOwnerForClient(Client client, DataLoader<Long, User> dataLoader) {
        return Mono.fromFuture(dataLoader.load(client.ownerUserId()));
    }

    @SchemaMapping(typeName = "Client", field = "clientGroup")
    public Mono<ClientGroup> getClientGroupForClient(Client client, DataLoader<Long, ClientGroup> dataLoader) {
        return Mono.fromFuture(dataLoader.load(client.clientGroupId()));
    }

    @SchemaMapping(typeName = "ClientLogin", field = "client")
    public Mono<Client> getClientForClientLogin(ClientLogin clientLogin, DataLoader<Long, Client> dataLoader) {
        return Mono.fromFuture(dataLoader.load(clientLogin.clientId()));
    }
}