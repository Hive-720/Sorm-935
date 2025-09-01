package com.kjsoft.sorm.infrastructure.persistence.data.client;

import com.kjsoft.sorm.infrastructure.persistence.data.client.entity.ClientLoginDb;
import com.kjsoft.sorm.infrastructure.persistence.data.client.entity.mapper.ClientLoginMapper;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper.ClientLoginFilterPersistenceMapper;
import com.kjsoft.sorm.service.whmcsdata.filter.ClientLoginFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.ClientRepository;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.dto.*;
import com.kjsoft.sorm.service.whmcsdata.filter.ClientFilter;
import com.kjsoft.sorm.service.whmcsdata.filter.ClientGroupFilter;
import com.kjsoft.sorm.service.whmcsdata.filter.ClientProductFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.ClientGroup;
import com.kjsoft.sorm.infrastructure.persistence.data.client.entity.ClientDb;
import com.kjsoft.sorm.infrastructure.persistence.data.client.entity.ClientGroupDb;
import com.kjsoft.sorm.infrastructure.persistence.data.client.entity.ClientProductDb;
import com.kjsoft.sorm.infrastructure.persistence.data.client.entity.mapper.ClientGroupPersistenceMapper;
import com.kjsoft.sorm.infrastructure.persistence.data.client.entity.mapper.ClientPersistenceMapper;
import com.kjsoft.sorm.infrastructure.persistence.data.client.entity.mapper.ClientProductPersistenceMapper;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper.ClientFilterPersistenceMapper;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper.ClientGroupFilterPersistenceMapper;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper.ClientProductFilterPersistenceMapper;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.ClientLogin;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.ClientProduct;
import io.r2dbc.postgresql.codec.Json;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kjsoft.sorm.infrastructure.persistence.util.PersistenceHelper.bindOrNull;


@Repository
@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {
    private final DatabaseClient databaseClient;
    private final DatabaseClientAdapter databaseClientAdapter;
    private final ClientPersistenceMapper clientMapper;
    private final ClientGroupPersistenceMapper clientGroupMapper;
    private final ClientProductPersistenceMapper clientProductMapper;
    private final ClientFilterPersistenceMapper clientFilterMapper;
    private final ClientGroupFilterPersistenceMapper clientGroupFilterMapper;
    private final ClientProductFilterPersistenceMapper clientProductFilterMapper;
    private final ClientLoginFilterPersistenceMapper clientLoginFilterMapper;
    private final ClientLoginMapper clientLoginMapper;


    @Override
    public Flux<ClientGroup> findAllClientGroup() {
        return databaseClient.sql("SELECT * FROM client_groups")
                .mapProperties(ClientGroupDb.class)
                .all()
                .map(clientGroupMapper::map);
    }

    @Override
    @Transactional
    public Mono<Boolean> addAllClient(List<Client> clients) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    INSERT INTO clients (
                        id, client_group_id, owner_user_id, full_name, first_name, last_name,
                        company_name, email, address, city, state, postcode, country,
                        msisdn, phonecc, phone_number, tax_id, telegram, jabber, currency_code,
                        default_gateway, status, credit, tax_exempt, language, date_created, deleted
                    ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16, $17, $18, $19, $20, $21, $22, $23, $24, $25, $26, $27)
                    """);

            return Flux.fromIterable(clients)
                    .map(clientMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getId());
                        bindOrNull(stmt, 1, entity.getClientGroupId(), Long.class);
                        bindOrNull(stmt, 2, entity.getOwnerUserId(), Long.class);
                        bindOrNull(stmt, 3, entity.getFullName(), String.class);
                        bindOrNull(stmt, 4, entity.getFirstName(), String.class);
                        bindOrNull(stmt, 5, entity.getLastName(), String.class);
                        bindOrNull(stmt, 6, entity.getCompanyName(), String.class);
                        bindOrNull(stmt, 7, entity.getEmail(), String.class);
                        bindOrNull(stmt, 8, entity.getAddress(), String.class);
                        bindOrNull(stmt, 9, entity.getCity(), String.class);
                        bindOrNull(stmt, 10, entity.getState(), String.class);
                        bindOrNull(stmt, 11, entity.getPostcode(), String.class);
                        bindOrNull(stmt, 12, entity.getCountry(), String.class);
                        bindOrNull(stmt, 13, entity.getMsisdn(), String.class);
                        bindOrNull(stmt, 14, entity.getPhonecc(), Integer.class);
                        bindOrNull(stmt, 15, entity.getPhoneNumber(), String.class);
                        bindOrNull(stmt, 16, entity.getTaxId(), String.class);
                        bindOrNull(stmt, 17, entity.getTelegram(), String.class);
                        bindOrNull(stmt, 18, entity.getJabber(), String.class);
                        bindOrNull(stmt, 19, entity.getCurrency(), String.class);
                        bindOrNull(stmt, 20, entity.getDefaultGateway(), String.class);
                        bindOrNull(stmt, 21, entity.getStatus(), String.class);
                        bindOrNull(stmt, 22, entity.getCredit(), BigDecimal.class);
                        bindOrNull(stmt, 23, entity.getTaxExempt(), Boolean.class);
                        bindOrNull(stmt, 24, entity.getLanguage(), String.class);
                        bindOrNull(stmt, 25, entity.getDateCreated(), OffsetDateTime.class);
                        bindOrNull(stmt, 26, entity.isDeleted(), Boolean.class);

                        if (index < clients.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == clients.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> addAllClientGroup(List<ClientGroup> clientGroups) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    INSERT INTO client_groups
                    (id, name, discount_percent, updated_at, deleted)
                    VALUES ($1, $2, $3, $4, $5)
                    """);

            return Flux.fromIterable(clientGroups)
                    .map(clientGroupMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getId());
                        bindOrNull(stmt, 1, entity.getName(), String.class);
                        bindOrNull(stmt, 2, entity.getDiscountPercent(), BigDecimal.class);
                        bindOrNull(stmt, 3, entity.getUpdatedAt(), OffsetDateTime.class);
                        bindOrNull(stmt, 4, entity.isDeleted(), Boolean.class);

                        if (index < clientGroups.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == clientGroups.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> updateAllClientGroup(List<ClientGroup> clientGroups) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    UPDATE affiliates
                    SET name = $1,
                        discount_percent = $2,
                        updated_at = $3,
                        deleted = $4
                    WHERE id = $5
                    """);

            return Flux.fromIterable(clientGroups)
                    .map(clientGroupMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        bindOrNull(stmt, 0, entity.getName(), String.class);
                        bindOrNull(stmt, 1, entity.getDiscountPercent(), BigDecimal.class);
                        bindOrNull(stmt, 2, entity.getUpdatedAt(), OffsetDateTime.class);
                        bindOrNull(stmt, 3, entity.isDeleted(), Boolean.class);
                        stmt.bind(4, entity.getId());

                        if (index < clientGroups.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == clientGroups.size());
        });
    }

    @Override
    public Mono<Boolean> softDeleteAllClient(Set<Long> ids) {
        return databaseClient.sql("UPDATE clients SET deleted = true WHERE id IN (:ids) AND deleted = false")
                .bind("ids", ids)
                .fetch()
                .rowsUpdated()
                .map(totalRows -> totalRows > 0);
    }

    @Override
    @Transactional
    public Mono<Boolean> addAllClientProduct(List<ClientProduct> clientProducts) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement(""" 
                    INSERT INTO client_products
                    (id, client_id, product_id, order_id, name, domain, dedicated_ip_address, first_payment_amount,
                    recurring_amount, billing_cycle, assigned_ips, status, configs, reg_date, next_due_date, last_update)
                    VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16)
                    """);

            return Flux.fromIterable(clientProducts)
                    .map(clientProductMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getId());
                        stmt.bind(1, entity.getClientId());
                        bindOrNull(stmt, 2, entity.getProductId(), Long.class);
                        bindOrNull(stmt, 3, entity.getOrderId(), Long.class);
                        bindOrNull(stmt, 4, entity.getName(), String.class);
                        bindOrNull(stmt, 5, entity.getDomain(), String.class);
                        bindOrNull(stmt, 6, entity.getDedicatedIpAddress(), String.class);
                        bindOrNull(stmt, 7, entity.getFirstPaymentAmount(), BigDecimal.class);
                        bindOrNull(stmt, 8, entity.getRecurringAmount(), BigDecimal.class);
                        bindOrNull(stmt, 9, entity.getBillingCycle(), String.class);
                        bindOrNull(stmt, 10, entity.getAssignedIps(), String[].class);
                        bindOrNull(stmt, 11, entity.getStatus(), String.class);
                        bindOrNull(stmt, 12, entity.getConfigs(), Json.class);
                        bindOrNull(stmt, 13, entity.getRegDate(), OffsetDateTime.class);
                        bindOrNull(stmt, 14, entity.getNextDueDate(), OffsetDateTime.class);
                        bindOrNull(stmt, 15, entity.getLastUpdate(), OffsetDateTime.class);

                        if (index < clientProducts.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == clientProducts.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> updateAllClientProduct(List<ClientProduct> clientProducts) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    UPDATE client_products
                    SET name = $1,
                        domain = $2,
                        dedicated_ip_address = $3,
                        first_payment_amount = $4,
                        recurring_amount = $5,
                        billing_cycle = $6,
                        status = $7,
                        assigned_ips = $8,
                        configs = $9,
                        next_due_date = $10,
                        last_update = $11
                    WHERE id = $12
                    """);

            return Flux.fromIterable(clientProducts)
                    .map(clientProductMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        bindOrNull(stmt, 0, entity.getName(), String.class);
                        bindOrNull(stmt, 1, entity.getDomain(), String.class);
                        bindOrNull(stmt, 2, entity.getDedicatedIpAddress(), String.class);
                        bindOrNull(stmt, 3, entity.getFirstPaymentAmount(), BigDecimal.class);
                        bindOrNull(stmt, 4, entity.getRecurringAmount(), BigDecimal.class);
                        bindOrNull(stmt, 5, entity.getBillingCycle(), String.class);
                        bindOrNull(stmt, 6, entity.getStatus(), String.class);
                        bindOrNull(stmt, 7, entity.getAssignedIps(), String[].class);
                        bindOrNull(stmt, 8, entity.getConfigs(), Json.class);
                        bindOrNull(stmt, 9, entity.getNextDueDate(), OffsetDateTime.class);
                        bindOrNull(stmt, 10, entity.getLastUpdate(), OffsetDateTime.class);
                        stmt.bind(11, entity.getId());

                        if (index < clientProducts.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == clientProducts.size());
        });
    }

    @Override
    public Flux<ClientLogin> findAllClientLastLoginByClientIdIn(Set<Long> ids) {
        return databaseClient.sql("""
                        SELECT
                            cl.*
                        FROM (
                            SELECT
                                *,
                                ROW_NUMBER() OVER (PARTITION BY client_id ORDER BY date DESC) as rn
                            FROM
                                client_logins
                            WHERE
                                client_id IN (:ids)
                        ) AS cl
                        WHERE
                            cl.rn = 1
                        """)
                .bind("ids", ids)
                .mapProperties(ClientLoginDb.class)
                .all()
                .map(clientLoginMapper::map);
    }

    @Override
    public Flux<ClientProduct> findAllClientProductByIdIn(Set<Long> ids) {
        return databaseClient.sql("SELECT * FROM client_products WHERE id IN(:ids)")
                .bind("ids", ids)
                .mapProperties(ClientProductDb.class)
                .all()
                .map(clientProductMapper::map);
    }

    @Override
    @Transactional
    public Mono<Boolean> updateAllClient(List<Client> clients) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    UPDATE clients
                    SET client_group_id = $1,
                        owner_user_id = $2,
                        full_name = $3,
                        first_name = $4,
                        last_name = $5,
                        company_name = $6,
                        email = $7,
                        address = $8,
                        city = $9,
                        state = $10,
                        postcode = $11,
                        country = $12,
                        msisdn = $13,
                        phonecc = $14,
                        phone_number = $15,
                        tax_id = $16,
                        telegram = $17,
                        jabber = $18,
                        currency_code = $19,
                        default_gateway = $20,
                        status = $21,
                        credit = $22,
                        tax_exempt = $23,
                        language = $24,
                        deleted = $25,
                    WHERE id = $26
                    """);

            return Flux.fromIterable(clients)
                    .map(clientMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        bindOrNull(stmt, 0, entity.getClientGroupId(), Long.class);
                        bindOrNull(stmt, 1, entity.getOwnerUserId(), Long.class);
                        bindOrNull(stmt, 2, entity.getFullName(), String.class);
                        bindOrNull(stmt, 3, entity.getFirstName(), String.class);
                        bindOrNull(stmt, 4, entity.getLastName(), String.class);
                        bindOrNull(stmt, 5, entity.getCompanyName(), String.class);
                        bindOrNull(stmt, 6, entity.getEmail(), String.class);
                        bindOrNull(stmt, 7, entity.getAddress(), String.class);
                        bindOrNull(stmt, 8, entity.getCity(), String.class);
                        bindOrNull(stmt, 9, entity.getState(), String.class);
                        bindOrNull(stmt, 10, entity.getPostcode(), String.class);
                        bindOrNull(stmt, 11, entity.getCountry(), String.class);
                        bindOrNull(stmt, 12, entity.getMsisdn(), String.class);
                        bindOrNull(stmt, 13, entity.getPhonecc(), Integer.class);
                        bindOrNull(stmt, 14, entity.getPhoneNumber(), String.class);
                        bindOrNull(stmt, 15, entity.getTaxId(), String.class);
                        bindOrNull(stmt, 16, entity.getTelegram(), String.class);
                        bindOrNull(stmt, 17, entity.getJabber(), String.class);
                        bindOrNull(stmt, 18, entity.getCurrency(), String.class);
                        bindOrNull(stmt, 19, entity.getDefaultGateway(), String.class);
                        bindOrNull(stmt, 20, entity.getStatus(), String.class);
                        bindOrNull(stmt, 21, entity.getCredit(), BigDecimal.class);
                        bindOrNull(stmt, 22, entity.getTaxExempt(), Boolean.class);
                        bindOrNull(stmt, 23, entity.getLanguage(), String.class);
                        bindOrNull(stmt, 24, entity.isDeleted(), Boolean.class);
                        stmt.bind(25, entity.getId());

                        if (index < clients.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == clients.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> addAllClientLogin(List<ClientLogin> clientLogins) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement(""" 
                    INSERT INTO client_logins
                    (client_id, ip_address, date)
                    VALUES ($1, $2, $3)
                    """);

            return Flux.fromIterable(clientLogins)
                    .map(clientLoginMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getClientId());
                        bindOrNull(stmt, 1, entity.getIpAddress(), String.class);
                        bindOrNull(stmt, 2, entity.getDate(), OffsetDateTime.class);

                        if (index < clientLogins.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == clientLogins.size());
        });
    }

    @Override
    public Flux<Client> findAllClientByIdIn(Set<Long> ids) {
        return databaseClient.sql("SELECT * FROM clients WHERE id IN(:ids)")
                .bind("ids", ids)
                .mapProperties(ClientDb.class)
                .all()
                .map(clientMapper::map);
    }

    @Override
    public Mono<ClientIdsResult> findAll(Long cursorById, int limit) {
        var sql = new StringBuilder("SELECT id FROM clients");

        var params = new ArrayList<>();

        if (cursorById != null) {
            sql.append(" WHERE id > :id");
            params.add(cursorById);
        }

        sql.append(" ORDER BY id ASC");
        sql.append(" LIMIT :limit");
        params.add(limit + 1);

        return databaseClient.sql(sql.toString())
                .bindValues(params)
                .fetch()
                .all()
                .map(row -> (Long) row.get("id"))
                .collectList()
                .map(ids -> {
                    var hasNext = false;
                    Long nextCursor = null;

                    if (ids.size() > limit) {
                        hasNext = true;
                        ids = ids.subList(0, limit);
                        nextCursor = !ids.isEmpty() ? ids.getLast() : null;
                    } else if (!ids.isEmpty()) {
                        nextCursor = ids.getLast();
                    }

                    return new ClientIdsResult(nextCursor, hasNext, ids);
                });
    }

    @Override
    public Flux<ClientGroup> findAllClientGroupByIdIn(Set<Long> ids) {
        return databaseClient.sql("SELECT * FROM client_groups WHERE id IN(:ids)")
                .bind("ids", ids)
                .mapProperties(ClientGroupDb.class)
                .all()
                .map(clientGroupMapper::map);
    }

    @Override
    public Mono<ClientSearchResult> findAllClientByFilter(ClientFilter filter, String cursor,
                                                          Integer offset, Integer limit) {
        var dbFilter = clientFilterMapper.map(filter);
        var limitPlusOne = limit + 1;

        return databaseClientAdapter.all(dbFilter, cursor, offset, limitPlusOne, ClientDb.class)
                .map(clientMapper::map)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    var hasNextPage = list.size() > limit;
                    var resultList = hasNextPage ? list.subList(0, limit) : list;
                    var lastUser = resultList.getLast();
                    var nextCursor = lastUser.getId().toString();
                    return new ClientSearchResult(nextCursor, hasNextPage, resultList);
                })
                .switchIfEmpty(Mono.just(new ClientSearchResult(null, false, List.of())));
    }

    @Override
    public Mono<ClientLoginSearchResult> findAllClientLoginByFilter(ClientLoginFilter filter, String cursor, Integer offset, Integer limit) {
        var dbFilter = clientLoginFilterMapper.map(filter);
        var limitPlusOne = limit + 1;

        return databaseClientAdapter.all(dbFilter, cursor, offset, limitPlusOne, ClientLoginDb.class)
                .map(clientLoginMapper::map)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    var hasNextPage = list.size() > limit;
                    var resultList = hasNextPage ? list.subList(0, limit) : list;
                    var lastUser = resultList.getLast();
                    var nextCursor = lastUser.getId().toString();
                    return new ClientLoginSearchResult(nextCursor, hasNextPage, resultList);
                })
                .switchIfEmpty(Mono.just(new ClientLoginSearchResult(null, false, List.of())));
    }

    @Override
    public Mono<ClientGroupSearchResult> findAllClientGroupByFilter(ClientGroupFilter filter, String cursor,
                                                                    Integer offset, Integer limit) {
        var dbFilter = clientGroupFilterMapper.map(filter);
        var limitPlusOne = limit + 1;

        return databaseClientAdapter.all(dbFilter, cursor, offset, limitPlusOne, ClientGroupDb.class)
                .map(clientGroupMapper::map)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    var hasNextPage = list.size() > limit;
                    var resultList = hasNextPage ? list.subList(0, limit) : list;
                    var lastUser = resultList.getLast();
                    var nextCursor = lastUser.getId().toString();
                    return new ClientGroupSearchResult(nextCursor, hasNextPage, resultList);
                })
                .switchIfEmpty(Mono.just(new ClientGroupSearchResult(null, false, List.of())));
    }

    @Override
    public Mono<ClientProductSearchResult> findAllClientProductByFilter(ClientProductFilter filter, String cursor,
                                                                        Integer offset, Integer limit) {
        var dbFilter = clientProductFilterMapper.map(filter);
        var limitPlusOne = limit + 1;

        return databaseClientAdapter.all(dbFilter, cursor, offset, limitPlusOne, ClientProductDb.class)
                .map(clientProductMapper::map)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    var hasNextPage = list.size() > limit;
                    var resultList = hasNextPage ? list.subList(0, limit) : list;
                    var lastUser = resultList.getLast();
                    var nextCursor = lastUser.getId().toString();
                    return new ClientProductSearchResult(nextCursor, hasNextPage, resultList);
                })
                .switchIfEmpty(Mono.just(new ClientProductSearchResult(null, false, List.of())));
    }
}