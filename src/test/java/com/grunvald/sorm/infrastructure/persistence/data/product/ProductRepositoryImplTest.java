package com.grunvald.sorm.infrastructure.persistence.data.product;

import com.grunvald.sorm.infrastructure.persistence.data.product.entity.ProductConfigDb;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.ProductConfigOptionDb;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.ProductDb;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.relation.ProductConfigRelationDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.product.mapper.ProductFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Price;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfig;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigRelation;
import io.r2dbc.postgresql.codec.Json;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.FetchSpec;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.grunvald.sorm.infrastructure.persistence.data.R2dbcMocks.mockInConnection;
import static com.grunvald.sorm.infrastructure.persistence.data.R2dbcMocks.mockStatementExecution;
import static com.grunvald.sorm.testutil.MapStructTestFactory.productConfigOptionPersistenceMapper;
import static com.grunvald.sorm.testutil.MapStructTestFactory.productConfigPersistenceMapper;
import static com.grunvald.sorm.testutil.MapStructTestFactory.productConfigRelationPersistenceMapper;
import static com.grunvald.sorm.testutil.MapStructTestFactory.productPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryImplTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    @Mock
    private DatabaseClient databaseClient;
    @Mock
    private DatabaseClientAdapter databaseClientAdapter;
    @Mock
    private Connection connection;
    @Mock
    private Statement statement;
    @Mock
    private ProductFilterPersistenceMapper productFilterMapper;

    private ProductRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new ProductRepositoryImpl(
                databaseClient, databaseClientAdapter,
                productPersistenceMapper(), productConfigPersistenceMapper(),
                productConfigOptionPersistenceMapper(), productConfigRelationPersistenceMapper(),
                productFilterMapper);
        lenient().when(connection.createStatement(anyString())).thenReturn(statement);
        lenient().when(statement.bind(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.bindNull(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.add()).thenReturn(statement);
        mockInConnection(databaseClient, connection);
    }

    private Price price() {
        var price = new Price();
        price.setCurrency("USD");
        price.setMonthly(new BigDecimal("10.00"));
        return price;
    }

    private Product product(Long id) {
        var product = new Product();
        product.setId(id);
        product.setType("hosting");
        product.setName("Hosting");
        product.setDescription("desc");
        product.setPayType("recurring");
        product.setPrices(List.of(price()));
        product.setUpdatedAt(DATE);
        product.setDeleted(false);
        return product;
    }

    private ProductConfig productConfig(Long id) {
        return new ProductConfig(id, "name", "type");
    }

    private ProductConfigOption productConfigOption(Long id) {
        var option = new ProductConfigOption();
        option.setId(id);
        option.setProductConfigId(10L);
        option.setName("RAM");
        option.setPrices(List.of(price()));
        return option;
    }

    private ProductConfigRelation relation(Long productId, Long configId) {
        return new ProductConfigRelation(productId, configId, DATE);
    }

    @Test
    void addAllProduct_bindsValuesExecutesInsertAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAllProduct(List.of(product(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO products"));
        assertTrue(sqlCaptor.getValue().contains("$8"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, "hosting");
        verify(statement).bind(2, "Hosting");
        verify(statement).bind(7, false);

        ArgumentCaptor<Object> pricesCaptor = ArgumentCaptor.forClass(Object.class);
        verify(statement).bind(eq(5), pricesCaptor.capture());
        assertTrue(pricesCaptor.getValue() instanceof Json json && json.asString().contains("USD"));
    }

    @Test
    void updateAllProduct_bindsValuesAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.updateAllProduct(List.of(product(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE products"));
        verify(statement).bind(0, "hosting");
        verify(statement).bind(1, "recurring");
        verify(statement).bind(6, 1L);
    }

    @Test
    void addAllProduct_bindsNullForMissingFields() {
        mockStatementExecution(statement, 1L);
        var product = product(1L);
        product.setPayType(null);
        product.setPrices(null);

        StepVerifier.create(repository.addAllProduct(List.of(product)))
                .expectNext(true)
                .verifyComplete();

        verify(statement).bindNull(4, String.class);
        verify(statement).bindNull(5, Json.class);
    }

    @Test
    void addAllConfig_bindsValuesAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAllConfig(List.of(productConfig(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO product_configs"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, "type");
        verify(statement).bind(2, "name");
    }

    @Test
    void updateAllConfig_multipleRowsChainsStatementsAndReturnsTrue() {
        mockStatementExecution(statement, 1L, 1L);

        StepVerifier.create(repository.updateAllConfig(List.of(productConfig(1L), productConfig(2L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE product_configs"));
        verify(statement).add();
        verify(statement).bind(2, 1L);
        verify(statement).bind(2, 2L);
    }

    @Test
    void deleteAllConfigByIdIn_returnsTrueWhenRowsUpdated() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        FetchSpec<Map<String, Object>> fetch = mock(FetchSpec.class);
        when(spec.fetch()).thenReturn(fetch);
        when(fetch.rowsUpdated()).thenReturn(Mono.just(2L));

        StepVerifier.create(repository.deleteAllConfigByIdIn(Set.of(1L, 2L)))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("DELETE FROM product_configs"));
    }

    @Test
    void deleteAllConfigByIdIn_returnsFalseWhenNoRowsUpdated() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        FetchSpec<Map<String, Object>> fetch = mock(FetchSpec.class);
        when(spec.fetch()).thenReturn(fetch);
        when(fetch.rowsUpdated()).thenReturn(Mono.empty());

        StepVerifier.create(repository.deleteAllConfigByIdIn(Set.of(1L)))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void addAllConfigOption_insertsIntoProductConfigOptionsTable() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAllConfigOption(List.of(productConfigOption(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO product_config_options"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, 10L);
        verify(statement).bind(2, "RAM");
    }

    @Test
    void addAllConfigOption_bindsNullForMissingFields() {
        mockStatementExecution(statement, 1L);
        var option = productConfigOption(1L);
        option.setName(null);
        option.setPrices(null);

        StepVerifier.create(repository.addAllConfigOption(List.of(option)))
                .expectNext(true)
                .verifyComplete();

        verify(statement).bindNull(2, String.class);
        verify(statement).bindNull(3, Json.class);
    }

    @Test
    void updateAllConfigOption_bindsValuesAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.updateAllConfigOption(List.of(productConfigOption(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE product_config_options"));
        verify(statement).bind(0, "RAM");
        verify(statement).bind(2, 1L);
    }

    @Test
    void deleteAllConfigOptionByIdIn_returnsTrueWhenRowsUpdated() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        FetchSpec<Map<String, Object>> fetch = mock(FetchSpec.class);
        when(spec.fetch()).thenReturn(fetch);
        when(fetch.rowsUpdated()).thenReturn(Mono.just(1L));

        StepVerifier.create(repository.deleteAllConfigOptionByIdIn(Set.of(1L)))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("DELETE FROM product_config_options"));
    }

    @Test
    void addAllProductConfigRelation_bindsValuesAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAllProductConfigRelation(List.of(relation(1L, 10L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO product_config_relations"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, 10L);
        verify(statement).bind(2, DATE);
    }

    @Test
    void deleteAllProductConfigRelation_bindsArraysAndReturnsTrueWhenRowsDeleted() {
        mockStatementExecution(statement, 2L);

        StepVerifier.create(repository.deleteAllProductConfigRelation(
                        List.of(relation(1L, 10L), relation(2L, 20L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("DELETE FROM product_config_relations"));

        ArgumentCaptor<Object> productIdsCaptor = ArgumentCaptor.forClass(Object.class);
        verify(statement).bind(eq(0), productIdsCaptor.capture());
        assertArrayEquals(new Long[]{1L, 2L}, (Long[]) productIdsCaptor.getValue());

        ArgumentCaptor<Object> configIdsCaptor = ArgumentCaptor.forClass(Object.class);
        verify(statement).bind(eq(1), configIdsCaptor.capture());
        assertArrayEquals(new Long[]{10L, 20L}, (Long[]) configIdsCaptor.getValue());
    }

    @Test
    void deleteAllProductConfigRelation_returnsFalseWhenNoRowsDeleted() {
        mockStatementExecution(statement, 0L);

        StepVerifier.create(repository.deleteAllProductConfigRelation(List.of(relation(1L, 10L))))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void findAllProduct_selectsAllAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);

        RowsFetchSpec<ProductDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(ProductDb.class)).thenReturn(rows);

        var db = new ProductDb();
        db.setId(1L);
        db.setName("Hosting");
        db.setPrices(Json.of("[{\"currency\":\"USD\",\"monthly\":10.00}]"));
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllProduct())
                .assertNext(product -> {
                    assertEquals(1L, product.getId());
                    assertEquals("USD", product.getPrices().getFirst().getCurrency());
                })
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("FROM products"));
    }

    @Test
    void findAllProductConfig_selectsAllAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);

        RowsFetchSpec<ProductConfigDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(ProductConfigDb.class)).thenReturn(rows);

        var db = new ProductConfigDb();
        db.setId(1L);
        db.setName("name");
        db.setType("type");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllProductConfig())
                .assertNext(config -> {
                    assertEquals(1L, config.getId());
                    assertEquals("type", config.getType());
                })
                .verifyComplete();
    }

    @Test
    void findAllProductConfigsByIdIn_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<ProductConfigDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(ProductConfigDb.class)).thenReturn(rows);
        when(rows.all()).thenReturn(Flux.empty());

        StepVerifier.create(repository.findAllProductConfigsByIdIn(Set.of(1L)))
                .verifyComplete();

        verify(spec).bind("ids", Set.of(1L));
    }

    @Test
    void findAllProductConfigOption_selectsAllAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);

        RowsFetchSpec<ProductConfigOptionDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(ProductConfigOptionDb.class)).thenReturn(rows);

        var db = new ProductConfigOptionDb();
        db.setId(1L);
        db.setName("RAM");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllProductConfigOption())
                .assertNext(option -> assertEquals("RAM", option.getName()))
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("FROM product_config_options"));
    }

    @Test
    void findAllProductByIdIn_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<ProductDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(ProductDb.class)).thenReturn(rows);

        var db = new ProductDb();
        db.setId(1L);
        db.setName("Hosting");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllProductByIdIn(Set.of(1L)))
                .assertNext(product -> assertEquals("Hosting", product.getName()))
                .verifyComplete();

        verify(spec).bind("ids", Set.of(1L));
    }

    @Test
    void findAllProductConfigOptionByProductConfigIdIn_filtersByProductConfigId() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<ProductConfigOptionDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(ProductConfigOptionDb.class)).thenReturn(rows);

        var db = new ProductConfigOptionDb();
        db.setId(1L);
        db.setProductConfigId(10L);
        db.setName("RAM");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllProductConfigOptionByProductConfigIdIn(Set.of(10L)))
                .assertNext(option -> assertEquals(10L, option.getProductConfigId()))
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("FROM product_config_options"));
        assertTrue(sqlCaptor.getValue().contains("product_config_id IN (:ids)"));
        verify(spec).bind("ids", Set.of(10L));
    }

    @Test
    void findAllProductConfigRelation_selectsAllAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);

        RowsFetchSpec<ProductConfigRelationDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(ProductConfigRelationDb.class)).thenReturn(rows);

        var db = new ProductConfigRelationDb();
        db.setProductId(1L);
        db.setProductConfigId(10L);
        db.setCreatedAt(DATE);
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllProductConfigRelation())
                .assertNext(relation -> {
                    assertEquals(1L, relation.getProductId());
                    assertEquals(10L, relation.getProductConfigId());
                })
                .verifyComplete();
    }

    @Test
    void findAllProductConfigRelationByProductIdIn_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<ProductConfigRelationDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(ProductConfigRelationDb.class)).thenReturn(rows);

        var db = new ProductConfigRelationDb();
        db.setProductId(1L);
        db.setProductConfigId(10L);
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllProductConfigRelationByProductIdIn(Set.of(1L)))
                .assertNext(relation -> assertEquals(10L, relation.getProductConfigId()))
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("product_id IN (:ids)"));
        verify(spec).bind("ids", Set.of(1L));
    }
}
