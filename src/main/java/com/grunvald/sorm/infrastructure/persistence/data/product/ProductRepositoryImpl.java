package com.grunvald.sorm.infrastructure.persistence.data.product;

import com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper.ProductConfigRelationPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.relation.ProductConfigRelationDb;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.ProductRepository;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.dto.ProductSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.ProductFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfig;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.ProductConfigDb;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.ProductConfigOptionDb;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.ProductDb;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper.ProductConfigOptionPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper.ProductConfigPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper.ProductPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.product.mapper.ProductFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigRelation;
import io.r2dbc.postgresql.codec.Json;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.grunvald.sorm.infrastructure.persistence.util.PersistenceHelper.bindOrNull;


@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final DatabaseClient databaseClient;
    private final DatabaseClientAdapter databaseClientAdapter;
    private final ProductPersistenceMapper productMapper;
    private final ProductConfigPersistenceMapper productConfigMapper;
    private final ProductConfigOptionPersistenceMapper productConfigOptionMapper;
    private final ProductConfigRelationPersistenceMapper productConfigRelationMapper;
    private final ProductFilterPersistenceMapper productFilterMapper;


    @Override
    @Transactional
    public Mono<Boolean> addAllProduct(List<Product> products) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement(""" 
                    INSERT INTO products
                    (id, type, name, description, pay_type, prices, updated_at, deleted)
                    VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
                    """);

            return Flux.fromIterable(products)
                    .map(productMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getId());
                        bindOrNull(stmt, 1, entity.getType(), String.class);
                        bindOrNull(stmt, 2, entity.getName(), String.class);
                        bindOrNull(stmt, 3, entity.getDescription(), String.class);
                        bindOrNull(stmt, 4, entity.getPayType(), String.class);
                        bindOrNull(stmt, 5, entity.getPrices(), Json.class);
                        bindOrNull(stmt, 6, entity.getUpdatedAt(), OffsetDateTime.class);
                        bindOrNull(stmt, 7, entity.isDeleted(), Boolean.class);

                        if (index < products.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == products.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> updateAllProduct(List<Product> products) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    UPDATE products
                    SET type = $1,
                        pay_type = $2,
                        name = $3,
                        description = $4,
                        prices = $5,
                        updated_at = $6
                    WHERE id = $7
                    """);

            return Flux.fromIterable(products)
                    .map(productMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        bindOrNull(stmt, 0, entity.getType(), String.class);
                        bindOrNull(stmt, 1, entity.getPayType(), String.class);
                        bindOrNull(stmt, 2, entity.getName(), String.class);
                        bindOrNull(stmt, 3, entity.getDescription(), String.class);
                        bindOrNull(stmt, 4, entity.getPrices(), Json.class);
                        bindOrNull(stmt, 5, entity.getUpdatedAt(), OffsetDateTime.class);
                        stmt.bind(6, entity.getId());

                        if (index < products.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == products.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> addAllConfig(List<ProductConfig> productConfigs) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement(""" 
                    INSERT INTO product_configs
                    (id, type, name)
                    VALUES ($1, $2, $3)
                    """);

            return Flux.fromIterable(productConfigs)
                    .map(productConfigMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getId());
                        bindOrNull(stmt, 1, entity.getType(), String.class);
                        bindOrNull(stmt, 2, entity.getName(), String.class);

                        if (index < productConfigs.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == productConfigs.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> updateAllConfig(List<ProductConfig> productConfigs) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    UPDATE product_configs
                    SET type = $1,
                        name = $2
                    WHERE id = $3
                    """);

            return Flux.fromIterable(productConfigs)
                    .map(productConfigMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        bindOrNull(stmt, 0, entity.getType(), String.class);
                        bindOrNull(stmt, 1, entity.getName(), String.class);
                        stmt.bind(2, entity.getId());

                        if (index < productConfigs.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == productConfigs.size());
        });
    }

    @Override
    public Mono<Boolean> deleteAllConfigByIdIn(Set<Long> ids) {
        return databaseClient.sql("DELETE FROM product_configs WHERE id IN (:ids)")
                .bind("ids", ids)
                .fetch()
                .rowsUpdated()
                .map(rows -> rows > 0)
                .defaultIfEmpty(false);
    }

    @Override
    @Transactional
    public Mono<Boolean> addAllConfigOption(List<ProductConfigOption> productConfigOptions) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement(""" 
                    INSERT INTO product_config_options
                    (id, product_config_id, name, prices)
                    VALUES ($1, $2, $3, $4)
                    """);

            return Flux.fromIterable(productConfigOptions)
                    .map(productConfigOptionMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getId());
                        stmt.bind(1, entity.getProductConfigId());
                        bindOrNull(stmt, 2, entity.getName(), String.class);
                        bindOrNull(stmt, 3, entity.getPrices(), Json.class);

                        if (index < productConfigOptions.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == productConfigOptions.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> updateAllConfigOption(List<ProductConfigOption> productConfigOptions) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    UPDATE product_config_options
                    SET name = $1,
                        prices = $2
                    WHERE id = $3
                    """);

            return Flux.fromIterable(productConfigOptions)
                    .map(productConfigOptionMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        bindOrNull(stmt, 0, entity.getName(), String.class);
                        bindOrNull(stmt, 1, entity.getPrices(), Json.class);
                        stmt.bind(2, entity.getId());

                        if (index < productConfigOptions.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == productConfigOptions.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> deleteAllConfigOptionByIdIn(Set<Long> ids) {
        return databaseClient.sql("DELETE FROM product_config_options WHERE id IN (:ids)")
                .bind("ids", ids)
                .fetch()
                .rowsUpdated()
                .map(rows -> rows > 0)
                .defaultIfEmpty(false);
    }

    @Override
    @Transactional
    public Mono<Boolean> addAllProductConfigRelation(List<ProductConfigRelation> relations) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement(""" 
                    INSERT INTO product_config_relations
                    (product_id, product_config_id, created_at)
                    VALUES ($1, $2, $3)
                    """);

            return Flux.fromIterable(relations)
                    .map(productConfigRelationMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getProductId());
                        stmt.bind(1, entity.getProductConfigId());
                        bindOrNull(stmt, 2, entity.getCreatedAt(), OffsetDateTime.class);

                        if (index < relations.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == relations.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> deleteAllProductConfigRelation(List<ProductConfigRelation> relations) {
        return databaseClient.inConnection(connection -> {
            var productIds = new ArrayList<Long>();
            var configIds = new ArrayList<Long>();

            for (var relation : relations) {
                productIds.add(relation.getProductId());
                configIds.add(relation.getProductConfigId());
            }
//TODO проверить
            var sql = """
                    DELETE FROM product_config_relations
                    WHERE (product_id, product_config_id) IN (
                        SELECT unnest(?::bigint[]), unnest(?::bigint[])
                    )
                    """;

            var statement = connection.createStatement(sql)
                    .bind(0, productIds.toArray(new Long[0]))
                    .bind(1, configIds.toArray(new Long[0]));

            return Mono.from(statement.execute())
                    .flatMap(result -> Mono.from(result.getRowsUpdated()))
                    .map(rows -> rows > 0);
        });
    }

    @Override
    public Mono<ProductSearchResult> findAllByFilter(ProductFilter filter, String cursor, Integer offset, Integer limit) {
        var dbFilter = productFilterMapper.map(filter);
        var limitPlusOne = limit + 1;

        return databaseClientAdapter.all(dbFilter, cursor, offset, limitPlusOne, ProductDb.class)
                .map(productMapper::map)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    var hasNextPage = list.size() > limit;
                    var resultList = hasNextPage ? list.subList(0, limit) : list;
                    var lastProduct = resultList.getLast();
                    var nextCursor = lastProduct.getId().toString();
                    return new ProductSearchResult(nextCursor, hasNextPage, resultList);
                })
                .switchIfEmpty(Mono.just(new ProductSearchResult(null, false, List.of())));
    }

    @Override
    public Flux<Product> findAllProduct() {
        return databaseClient.sql("SELECT * FROM products")
                .mapProperties(ProductDb.class)
                .all()
                .map(productMapper::map);
    }

    @Override
    public Flux<ProductConfig> findAllProductConfig() {
        return databaseClient.sql("SELECT * FROM product_configs")
                .mapProperties(ProductConfigDb.class)
                .all()
                .map(productConfigMapper::map);
    }

    @Override
    public Flux<ProductConfig> findAllProductConfigsByIdIn(Set<Long> ids) {
        return databaseClient.sql("SELECT * FROM product_configs WHERE id IN (:ids)")
                .bind("ids", ids)
                .mapProperties(ProductConfigDb.class)
                .all()
                .map(productConfigMapper::map);
    }

    @Override
    public Flux<ProductConfigOption> findAllProductConfigOption() {
        return databaseClient.sql("SELECT * FROM product_config_options")
                .mapProperties(ProductConfigOptionDb.class)
                .all()
                .map(productConfigOptionMapper::map);
    }

    @Override
    public Flux<Product> findAllProductByIdIn(Set<Long> ids) {
        return databaseClient.sql("SELECT * FROM products WHERE id IN(:ids)")
                .bind("ids", ids)
                .mapProperties(ProductDb.class)
                .all()
                .map(productMapper::map);
    }

    @Override
    public Flux<ProductConfigOption> findAllProductConfigOptionByProductConfigIdIn(Set<Long> ids) {
        return databaseClient.sql("""
                        SELECT * FROM product_config_options
                        WHERE product_config_id IN (:ids)
                        """)
                .bind("ids", ids)
                .mapProperties(ProductConfigOptionDb.class)
                .all()
                .map(productConfigOptionMapper::map);
    }

    @Override
    public Flux<ProductConfigRelation> findAllProductConfigRelation() {
        return databaseClient.sql("SELECT * FROM product_config_relations")
                .mapProperties(ProductConfigRelationDb.class)
                .all()
                .map(productConfigRelationMapper::map);
    }

    @Override
    public Flux<ProductConfigRelation> findAllProductConfigRelationByProductIdIn(Set<Long> ids) {
        return databaseClient.sql("SELECT * FROM product_config_relations WHERE product_id IN (:ids)")
                .bind("ids", ids)
                .mapProperties(ProductConfigRelationDb.class)
                .all()
                .map(productConfigRelationMapper::map);
    }
}