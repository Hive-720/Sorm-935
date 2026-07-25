package com.grunvald.sorm.infrastructure.persistence.data.contact;

import com.grunvald.sorm.service.whmcsdata.dataservice.contact.ContactRepository;
import com.grunvald.sorm.service.whmcsdata.filter.ContactFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact;
import com.grunvald.sorm.service.whmcsdata.dataservice.contact.dto.ContactSearchResult;
import com.grunvald.sorm.infrastructure.persistence.data.contact.entity.ContactDb;
import com.grunvald.sorm.infrastructure.persistence.data.contact.entity.mapper.ContactPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.contact.mapper.ContactFilterPersistenceMapper;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.grunvald.sorm.infrastructure.persistence.util.PersistenceHelper.bindOrNull;


@Repository
@RequiredArgsConstructor
public class ContactRepositoryImpl implements ContactRepository {
    private final DatabaseClient databaseClient;
    private final DatabaseClientAdapter databaseClientAdapter;
    private final ContactPersistenceMapper contactMapper;
    private final ContactFilterPersistenceMapper contactFilterMapper;


    @Override
    public Flux<Long> getAllIds() {
        return databaseClient.sql("SELECT id FROM contacts")
                .fetch()
                .all()
                .map(row -> (Long) row.get("id"));
    }

    @Override
    @Transactional
    public Mono<Boolean> addAll(List<Contact> contacts) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement(""" 
                    INSERT INTO contacts
                    (id, client_id, full_name, first_name, last_name, company_name, email, address,
                    city, state, postcode, country, msisdn, tax_id, created_at, updated_at)
                    VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16)
                    """);

            return Flux.fromIterable(contacts)
                    .map(contactMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getId());
                        bindOrNull(stmt, 1, entity.getClientId(), Long.class);
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
                        bindOrNull(stmt, 13, entity.getTaxId(), String.class);
                        bindOrNull(stmt, 14, entity.getCreatedAt(), OffsetDateTime.class);
                        bindOrNull(stmt, 15, entity.getUpdatedAt(), OffsetDateTime.class);

                        if (index < contacts.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == contacts.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> updateAll(List<Contact> contacts) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    UPDATE contacts
                    SET full_name = $1,
                        first_name = $2,
                        last_name = $3,
                        company_name = $4,
                        email = $5,
                        address = $6,
                        city = $7,
                        state = $8,
                        postcode = $9,
                        country = $10,
                        msisdn = $11,
                        tax_id = $12,
                        updated_at = $13
                    WHERE id = $14
                    """);

            return Flux.fromIterable(contacts)
                    .map(contactMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        bindOrNull(stmt, 0, entity.getFullName(), String.class);
                        bindOrNull(stmt, 1, entity.getFirstName(), String.class);
                        bindOrNull(stmt, 2, entity.getLastName(), String.class);
                        bindOrNull(stmt, 3, entity.getCompanyName(), String.class);
                        bindOrNull(stmt, 4, entity.getEmail(), String.class);
                        bindOrNull(stmt, 5, entity.getAddress(), String.class);
                        bindOrNull(stmt, 6, entity.getCity(), String.class);
                        bindOrNull(stmt, 7, entity.getState(), String.class);
                        bindOrNull(stmt, 8, entity.getPostcode(), String.class);
                        bindOrNull(stmt, 9, entity.getCountry(), String.class);
                        bindOrNull(stmt, 10, entity.getMsisdn(), String.class);
                        bindOrNull(stmt, 11, entity.getTaxId(), String.class);
                        bindOrNull(stmt, 12, entity.getUpdatedAt(), OffsetDateTime.class);
                        stmt.bind(13, entity.getId());

                        if (index < contacts.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == contacts.size());
        });
    }

    @Override
    public Mono<Boolean> softDeleteAll(Set<Long> ids) {
        return databaseClient.sql("UPDATE contacts SET deleted = true WHERE id IN (:ids) AND deleted = false")
                .fetch()
                .rowsUpdated()
                .map(totalRows -> totalRows > 0);
    }

    @Override
    public Flux<Contact> findAllByIdIn(Set<Long> ids) {
        return databaseClient.sql("SELECT * FROM contacts WHERE id IN(:ids)")
                .bind("ids", ids)
                .mapProperties(ContactDb.class)
                .all()
                .map(contactMapper::map);
    }

    @Override
    public Mono<ContactSearchResult> findAllByFilter(ContactFilter filter, String cursor, Integer offset, Integer limit) {
        var dbFilter = contactFilterMapper.map(filter);
        var limitPlusOne = limit + 1;

        return databaseClientAdapter.all(dbFilter, cursor, offset, limitPlusOne, ContactDb.class)
                .map(contactMapper::map)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    var hasNextPage = list.size() > limit;
                    var resultList = hasNextPage ? list.subList(0, limit) : list;
                    var lastUser = resultList.getLast();
                    var nextCursor = lastUser.getId().toString();
                    return new ContactSearchResult(nextCursor, hasNextPage, resultList);
                })
                .switchIfEmpty(Mono.just(new ContactSearchResult(null, false, List.of())));
    }
}