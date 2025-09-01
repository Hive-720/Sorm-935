package com.kjsoft.sorm.infrastructure.persistence.datafilter;

import lombok.AllArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;


@Component
@AllArgsConstructor
public class DatabaseClientAdapter {
    private final DatabaseClient databaseClient;
    private final QueryDslSqlGenerator sqlGenerator;

    public <T> Flux<T> all(EntityFilter filter, String cursor, Integer offset, Integer limit, Class<T> clazz) {
        var generated = sqlGenerator.generateSelect(filter, cursor, offset, limit);

        var spec = databaseClient.sql(generated.getLeft());
        var params = generated.getRight();
        for (var i = 0; i < params.size(); i++) {
            spec = spec.bind(i, params.get(i));
        }

        return spec.mapProperties(clazz)
                .all();
    }
}