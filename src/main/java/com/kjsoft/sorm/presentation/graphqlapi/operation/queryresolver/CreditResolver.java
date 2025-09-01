package com.kjsoft.sorm.presentation.graphqlapi.operation.queryresolver;

import com.kjsoft.sorm.service.whmcsdata.dataservice.credit.CreditService;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.CreditFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.CreditFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.CreditResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.CreditResultMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Credit;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;


@Controller
@RequiredArgsConstructor
public class CreditResolver {
    private final CreditService creditService;
    private final CreditFilterPresentationMapper creditFilterMapper;
    private final CreditResultMapper creditResultMapper;


    @SchemaMapping(typeName = "QueryMessage")
    public Mono<CreditResult> credits(@Argument CreditFilterInput filter, @Argument Integer offset,
                                      @Argument Integer limit, @Argument String cursor) {
        var searchFilter = creditFilterMapper.map(filter);
        return creditService.search(searchFilter, cursor, offset, limit)
                .map(creditResultMapper::map);
    }

    @SchemaMapping(typeName = "Credit", field = "client")
    public Mono<Client> getClientForCredit(Credit credit, DataLoader<Long, Client> dataLoader) {
        return Mono.fromFuture(dataLoader.load(credit.clientId()));
    }
}