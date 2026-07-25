package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.User;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.UserService;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.UserFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.UserFilterPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.UserResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.UserResultMapper;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class UserResolver {
    private final UserService userService;
    private final UserFilterPresentationMapper userFilterMapper;
    private final UserResultMapper userResultMapper;


    @SchemaMapping(typeName = "QueryMessage")
    public Mono<UserResult> users(@Argument UserFilterInput filter, @Argument Integer offset, @Argument Integer limit,
                                  @Argument String cursor) {
        var searchFilter = userFilterMapper.map(filter);
        return userService.search(searchFilter, cursor, offset, limit)
                .map(userResultMapper::map);
    }

    @SchemaMapping(typeName = "User", field = "clients")
    public Mono<List<Client>> getProductConfigsForProduct(User user,
                                                          DataLoader<Long, List<Client>> dataLoader) {
        return Mono.fromFuture(dataLoader.load(user.id()));
    }
}