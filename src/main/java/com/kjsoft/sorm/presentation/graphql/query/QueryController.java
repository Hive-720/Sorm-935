package com.kjsoft.sorm.presentation.graphql.query;

import com.kjsoft.sorm.presentation.graphql.query.filters.CommentFilterInput;
import com.kjsoft.sorm.presentation.graphql.query.filters.UserFilterInput;
import com.kjsoft.sorm.presentation.graphql.query.records.Comment;
import com.kjsoft.sorm.presentation.graphql.query.records.User;
import com.kjsoft.sorm.presentation.graphql.query.result.QueryMessage;
import com.kjsoft.sorm.presentation.graphql.query.result.CommentResult;
import com.kjsoft.sorm.presentation.graphql.query.result.UserResult;
import graphql.schema.idl.SchemaPrinter;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class QueryController {

    //TODO пока данные выдуманы!!!!!

    private final GraphQlSource graphQlSource;
    private final SchemaPrinter clearSchemaPrinter;

    @QueryMapping
    public Mono<String> getSchema() {
        var schemaSDL = clearSchemaPrinter.print(graphQlSource.schema());
        return Mono.just(schemaSDL);
    }

    @QueryMapping
    public Mono<QueryMessage> request() {
        //TODO всегда ли сохранять входящее сообщение? Или только в рамках отложенных запросов?
        return Mono.just(new QueryMessage());
    }

    @SchemaMapping(typeName = "QueryMessage", field = "users")
    public Mono<UserResult> users(@Argument UserFilterInput filter, @Argument Integer offset, @Argument Integer limit) {
        return Mono.just(new UserResult(false, List.of(new User("1", "Arcadi", null))));
    }

    @SchemaMapping(typeName = "QueryMessage", field = "comments")
    public Mono<CommentResult> comments(@Argument CommentFilterInput filter, @Argument Integer offset, @Argument Integer limit) {
        return Mono.just(new CommentResult(false, List.of(new Comment("1", "1", "first comm"), new Comment("2", "1", "second comm"))));
    }

    //TODO это для вложенных(может таких и не будет), в ответе пока не ясно как быть с пагинацией в этом случае (хоят там была темка про курсорную)
    @SchemaMapping(typeName = "User", field = "comments")
    public Mono<CommentResult> comments(User user, @Argument Integer offset, @Argument Integer limit) {
        return Mono.just(
                new CommentResult(false, List.of(new Comment("1", "1", "first comm"),
                        new Comment("2", "1", "second comm")))
        );

    }
}