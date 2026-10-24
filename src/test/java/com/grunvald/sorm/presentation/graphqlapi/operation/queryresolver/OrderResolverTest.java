package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.OrderFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Contact;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Invoice;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Order;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.OrderItem;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.OrderService;
import com.grunvald.sorm.service.whmcsdata.dataservice.order.dto.OrderSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.OrderFilter;
import com.grunvald.sorm.testutil.MapStructTestFactory;
import org.dataloader.DataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderResolverTest {

    @Mock
    private OrderService orderService;

    @Mock
    private DataLoader<Long, List<OrderItem>> itemsDataLoader;

    @Mock
    private DataLoader<Long, Client> clientDataLoader;

    @Mock
    private DataLoader<Long, Invoice> invoiceDataLoader;

    @Mock
    private DataLoader<Long, Contact> contactDataLoader;

    private final FilterMappers filterMappers = new FilterMappers();

    private OrderResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new OrderResolver(orderService, filterMappers.orderMapper,
                MapStructTestFactory.orderResultMapper());
    }

    private OrderFilterInput sampleFilter() {
        OrderFilterInput nestedAnd = new OrderFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D11), null, null, null);
        OrderFilterInput nestedOr = new OrderFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D12), null, null, null);
        OrderFilterInput nestedNot = new OrderFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D13), null, null, null);
        return new OrderFilterInput(
                SampleFilters.client(),
                SampleFilters.invoice(),
                SampleFilters.ipAddress(),
                SampleFilters.orderItem(),
                SampleFilters.dateTime(SampleFilters.D08),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);
    }

    @Test
    void orders_mapsFilterAndPassesPaginationArgumentsInOrder() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.order.model.Order();
        model.setId(1L);
        model.setClientId(2L);
        model.setContactId(3L);
        model.setInvoiceId(4L);
        when(orderService.searchOrders(any(), any(), any(), any()))
                .thenReturn(Mono.just(new OrderSearchResult("cur-12", true, List.of(model))));

        StepVerifier.create(resolver.orders(sampleFilter(), 6, 66, "cur-12"))
                .assertNext(result -> {
                    assertEquals("cur-12", result.cursor());
                    assertTrue(result.hasNextPage());
                    assertEquals(1, result.result().size());
                    Order first = result.result().getFirst();
                    assertEquals(1L, first.id());
                    assertEquals(2L, first.clientId());
                    assertEquals(3L, first.contactId());
                    assertEquals(4L, first.invoiceId());
                })
                .verifyComplete();

        ArgumentCaptor<OrderFilter> filterCaptor = ArgumentCaptor.forClass(OrderFilter.class);
        verify(orderService).searchOrders(filterCaptor.capture(), eq("cur-12"), eq(6), eq(66));
        OrderFilter filter = filterCaptor.getValue();
        assertNotNull(filter.getClient());
        assertEquals("exact-eq", filter.getClient().getName().getFirstName().getEq());
        assertNotNull(filter.getInvoice());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), filter.getInvoice().getDate().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), filter.getInvoice().getDueDate().getUtc().getEq());
        assertNotNull(filter.getItems());
        assertEquals("a.ru", filter.getItems().getDomain().getDomainName().getEq());
        assertEquals("10.0.0.1", filter.getIpAddress().getIp().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D08), filter.getDate().getUtc().getEq());
        assertEquals(1, filter.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), filter.getAnd().get(0).getDate().getUtc().getEq());
        assertEquals(1, filter.getOr().size());
        assertNotNull(filter.getNot());
    }

    @Test
    void orders_passesNullArgumentsThrough() {
        when(orderService.searchOrders(any(), any(), any(), any()))
                .thenReturn(Mono.just(new OrderSearchResult(null, false, List.of())));

        StepVerifier.create(resolver.orders(null, null, null, null))
                .assertNext(result -> {
                    assertNull(result.cursor());
                    assertFalse(result.hasNextPage());
                    assertEquals(0, result.result().size());
                })
                .verifyComplete();

        verify(orderService).searchOrders(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void orders_propagatesServiceError() {
        when(orderService.searchOrders(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("order boom")));

        StepVerifier.create(resolver.orders(sampleFilter(), 0, 10, null))
                .expectErrorSatisfies(e -> assertEquals("order boom", e.getMessage()))
                .verify();
    }

    @Test
    void orders_emptyServiceResultCompletesEmpty() {
        when(orderService.searchOrders(any(), any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(resolver.orders(sampleFilter(), 0, 10, null))
                .verifyComplete();
    }

    @Test
    void getItemsForOrder_loadsItemsByOrderId() {
        List<OrderItem> items = List.of(new OrderItem(1L, "hosting", null, null, null, null, null, null, null));
        when(itemsDataLoader.load(1L)).thenReturn(CompletableFuture.completedFuture(items));

        Order order = new Order(1L, 2L, 3L, 4L, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getItemsForOrder(order, itemsDataLoader))
                .expectNext(items)
                .verifyComplete();

        verify(itemsDataLoader).load(1L);
    }

    @Test
    void getItemsForOrder_nullResultCompletesEmpty() {
        when(itemsDataLoader.load(1L)).thenReturn(CompletableFuture.completedFuture(null));

        Order order = new Order(1L, 2L, 3L, 4L, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getItemsForOrder(order, itemsDataLoader))
                .verifyComplete();
    }

    @Test
    void getClientForOrder_loadsClientByClientId() {
        Client client = new Client(2L, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
        when(clientDataLoader.load(2L)).thenReturn(CompletableFuture.completedFuture(client));

        Order order = new Order(1L, 2L, 3L, 4L, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getClientForOrder(order, clientDataLoader))
                .expectNext(client)
                .verifyComplete();

        verify(clientDataLoader).load(2L);
    }

    @Test
    void getInvoiceForOrder_loadsInvoiceByInvoiceId() {
        Invoice invoice = new Invoice(4L, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
        when(invoiceDataLoader.load(4L)).thenReturn(CompletableFuture.completedFuture(invoice));

        Order order = new Order(1L, 2L, 3L, 4L, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getInvoiceForOrder(order, invoiceDataLoader))
                .expectNext(invoice)
                .verifyComplete();

        verify(invoiceDataLoader).load(4L);
    }

    @Test
    void getInvoiceForOrder_nullResultCompletesEmpty() {
        when(invoiceDataLoader.load(4L)).thenReturn(CompletableFuture.completedFuture(null));

        Order order = new Order(1L, 2L, 3L, 4L, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getInvoiceForOrder(order, invoiceDataLoader))
                .verifyComplete();
    }

    @Test
    void getContactForOrder_loadsContactByContactId() {
        Contact contact = new Contact(3L, null, null, null, null, null, null, null, null, null, null);
        when(contactDataLoader.load(3L)).thenReturn(CompletableFuture.completedFuture(contact));

        Order order = new Order(1L, 2L, 3L, 4L, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getContactForOrder(order, contactDataLoader))
                .expectNext(contact)
                .verifyComplete();

        verify(contactDataLoader).load(3L);
    }
}
