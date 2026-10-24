package com.grunvald.sorm.infrastructure.persistence.data.order;

import com.grunvald.sorm.infrastructure.persistence.data.order.entity.OrderDb;
import com.grunvald.sorm.infrastructure.persistence.data.order.entity.OrderItemDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order.mapper.OrderFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.Order;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem;
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
import static com.grunvald.sorm.testutil.MapStructTestFactory.orderItemPersistenceMapper;
import static com.grunvald.sorm.testutil.MapStructTestFactory.orderPersistenceMapper;
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
class OrderRepositoryImplTest {

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
    private OrderFilterPersistenceMapper orderFilterMapper;

    private OrderRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new OrderRepositoryImpl(
                databaseClient, databaseClientAdapter,
                orderPersistenceMapper(), orderItemPersistenceMapper(), orderFilterMapper);
        lenient().when(connection.createStatement(anyString())).thenReturn(statement);
        lenient().when(statement.bind(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.bindNull(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.add()).thenReturn(statement);
        mockInConnection(databaseClient, connection);
    }

    private Order order(Long id) {
        var order = new Order();
        order.setId(id);
        order.setClientId(10L);
        order.setContactId(20L);
        order.setInvoiceId(30L);
        order.setAmount(new BigDecimal("50.00"));
        order.setCurrency("USD");
        order.setStatus("Pending");
        order.setIpAddress("127.0.0.1");
        order.setPaymentMethod("paypal");
        order.setPaymentMethodName("PayPal");
        order.setPaymentStatus("Unpaid");
        order.setDate(DATE);
        return order;
    }

    private OrderItem orderItem(Long id) {
        var item = new OrderItem();
        item.setId(id);
        item.setOrderId(10L);
        item.setRelId(20L);
        item.setType("Hosting");
        item.setProductType("server");
        item.setProductName("VPS");
        item.setDomain("example.com");
        item.setBillingCycle(new BigDecimal("1"));
        item.setAmount(new BigDecimal("25.00"));
        item.setStatus("Active");
        return item;
    }

    @Test
    void addAllOrder_bindsValuesExecutesInsertAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAllOrder(List.of(order(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO orders"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, 10L);
        verify(statement).bind(2, 20L);
        verify(statement).bind(4, new BigDecimal("50.00"));
    }

    @Test
    void addAllOrder_bindsNullForMissingFields() {
        mockStatementExecution(statement, 1L);
        var order = order(1L);
        order.setContactId(null);
        order.setInvoiceId(null);

        StepVerifier.create(repository.addAllOrder(List.of(order)))
                .expectNext(true)
                .verifyComplete();

        verify(statement).bindNull(2, Long.class);
        verify(statement).bindNull(3, Long.class);
    }

    @Test
    void updateAllOrder_multipleRowsChainsStatementsAndReturnsTrue() {
        mockStatementExecution(statement, 1L, 1L);

        StepVerifier.create(repository.updateAllOrder(List.of(order(1L), order(2L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE orders"));
        verify(statement).add();
        verify(statement).bind(7, 1L);
        verify(statement).bind(7, 2L);
    }

    @Test
    void addAllOrderItem_bindsValuesAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAllOrderItem(List.of(orderItem(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO order_items"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, 10L);
        verify(statement).bind(7, new BigDecimal("1"));
        verify(statement).bind(8, new BigDecimal("25.00"));
    }

    @Test
    void addAllOrderItem_bindsNullForMissingFields() {
        mockStatementExecution(statement, 1L);
        var item = orderItem(1L);
        item.setRelId(null);
        item.setBillingCycle(null);

        StepVerifier.create(repository.addAllOrderItem(List.of(item)))
                .expectNext(true)
                .verifyComplete();

        verify(statement).bindNull(2, Long.class);
        verify(statement).bindNull(7, BigDecimal.class);
    }

    @Test
    void updateAllOrderItem_bindsValuesAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.updateAllOrderItem(List.of(orderItem(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE order_items"));
        verify(statement).bind(0, "Hosting");
        verify(statement).bind(4, new BigDecimal("1"));
        verify(statement).bind(7, 1L);
    }

    @Test
    void deleteAllOrderItemByIdIn_returnsTrueWhenRowsUpdated() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        FetchSpec<Map<String, Object>> fetch = mock(FetchSpec.class);
        when(spec.fetch()).thenReturn(fetch);
        when(fetch.rowsUpdated()).thenReturn(Mono.just(2L));

        StepVerifier.create(repository.deleteAllOrderItemByIdIn(Set.of(1L, 2L)))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("DELETE FROM order_items"));
        verify(spec).bind("ids", Set.of(1L, 2L));
    }

    @Test
    void deleteAllOrderItemByIdIn_returnsFalseWhenNoRowsUpdated() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        FetchSpec<Map<String, Object>> fetch = mock(FetchSpec.class);
        when(spec.fetch()).thenReturn(fetch);
        when(fetch.rowsUpdated()).thenReturn(Mono.empty());

        StepVerifier.create(repository.deleteAllOrderItemByIdIn(Set.of(1L)))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void findAllOrderByIdIn_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<OrderDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(OrderDb.class)).thenReturn(rows);

        var db = new OrderDb();
        db.setId(1L);
        db.setClientId(10L);
        db.setStatus("Pending");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllOrderByIdIn(Set.of(1L)))
                .assertNext(order -> {
                    assertEquals(1L, order.getId());
                    assertEquals("Pending", order.getStatus());
                })
                .verifyComplete();

        verify(spec).bind("ids", Set.of(1L));
    }

    @Test
    void findAllOrderItemByOrderIds_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<OrderItemDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(OrderItemDb.class)).thenReturn(rows);

        var db = new OrderItemDb();
        db.setId(1L);
        db.setOrderId(10L);
        db.setProductName("VPS");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllOrderItemByOrderIds(Set.of(10L)))
                .assertNext(item -> {
                    assertEquals(1L, item.getId());
                    assertEquals("VPS", item.getProductName());
                })
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("FROM order_items"));
        verify(spec).bind("ids", Set.of(10L));
    }
}
