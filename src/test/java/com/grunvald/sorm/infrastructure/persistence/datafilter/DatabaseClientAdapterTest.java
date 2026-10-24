package com.grunvald.sorm.infrastructure.persistence.datafilter;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.paymentmethod.PaymentMethodPersistenceFilter;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DatabaseClientAdapterTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");
    private static final String SQL = "select * from payMd where updated_at = $1 and updated_at = $2";

    private final DatabaseClient databaseClient = mock(DatabaseClient.class);
    private final QueryDslSqlGenerator sqlGenerator = mock(QueryDslSqlGenerator.class);
    private final DatabaseClientAdapter adapter = new DatabaseClientAdapter(databaseClient, sqlGenerator);

    @Test
    @SuppressWarnings("unchecked")
    void all_generatesSqlBindsParamsAndMapsProperties() {
        var filter = new PaymentMethodPersistenceFilter();
        when(sqlGenerator.generateSelect(filter, null, null, 10))
                .thenReturn(Pair.of(SQL, List.of(DATE, DATE.plusDays(1))));

        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(SQL)).thenReturn(spec);
        when(spec.bind(0, DATE)).thenReturn(spec);
        when(spec.bind(1, DATE.plusDays(1))).thenReturn(spec);

        var rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(String.class)).thenReturn(rows);
        when(rows.all()).thenReturn(Flux.just("row"));

        var result = adapter.all(filter, null, null, 10, String.class).collectList().block();

        assertEquals(List.of("row"), result);
        verify(sqlGenerator).generateSelect(filter, null, null, 10);
        verify(databaseClient).sql(SQL);
        verify(spec).bind(0, DATE);
        verify(spec).bind(1, DATE.plusDays(1));
        verify(spec).mapProperties(String.class);
        verify(rows).all();
    }
}
