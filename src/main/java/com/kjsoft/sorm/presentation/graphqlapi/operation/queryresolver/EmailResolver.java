package com.kjsoft.sorm.presentation.graphqlapi.operation.queryresolver;

import com.kjsoft.sorm.service.whmcsdata.dataservice.email.EmailService;
import com.kjsoft.sorm.graphql.generated.model.EmailFilterInputGql;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.*;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Email;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.EmailResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.EmailResultMapper;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;


@Controller
@RequiredArgsConstructor
public class EmailResolver {
    private final EmailService emailService;
    private final EmailFilterPresentationMapper emailFilterMapper;
    private final EmailResultMapper emailResultMapper;


    @SchemaMapping(typeName = "QueryMessage")
    public Mono<EmailResult> emails(@Argument EmailFilterInputGql filter, @Argument Integer offset,
                                    @Argument Integer limit, @Argument String cursor) {
        var searchFilter = emailFilterMapper.map(filter);
        return emailService.search(searchFilter, cursor, offset, limit)
                .map(emailResultMapper::map);
    }

    @SchemaMapping(typeName = "Email", field = "client")
    public Mono<Client> getClientForEmail(Email email, DataLoader<Long, Client> dataLoader) {
        return Mono.fromFuture(dataLoader.load(email.clientId()));
    }
}