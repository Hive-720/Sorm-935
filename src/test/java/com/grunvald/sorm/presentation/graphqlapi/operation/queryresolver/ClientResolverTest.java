package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientGroupFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientLoginFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientProductFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.IpAddressBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ClientGroup;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ClientLogin;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ClientProduct;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Order;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Product;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.User;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientGroupSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientLoginSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientProductSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.ClientFilter;
import com.grunvald.sorm.service.whmcsdata.filter.ClientGroupFilter;
import com.grunvald.sorm.service.whmcsdata.filter.ClientLoginFilter;
import com.grunvald.sorm.service.whmcsdata.filter.ClientProductFilter;
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
class ClientResolverTest {

    @Mock
    private ClientService clientService;

    @Mock
    private DataLoader<Long, Product> productDataLoader;

    @Mock
    private DataLoader<Long, Order> orderDataLoader;

    @Mock
    private DataLoader<Long, Client> clientDataLoader;

    @Mock
    private DataLoader<Long, User> userDataLoader;

    @Mock
    private DataLoader<Long, ClientGroup> clientGroupDataLoader;

    private final FilterMappers filterMappers = new FilterMappers();

    private ClientResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new ClientResolver(clientService, filterMappers.clientMapper,
                filterMappers.clientProductMapper, filterMappers.clientGroupMapper,
                filterMappers.clientLoginMapper,
                MapStructTestFactory.clientResultMapper(),
                MapStructTestFactory.clientProductResultMapper(),
                MapStructTestFactory.clientGroupResultMapper(),
                MapStructTestFactory.clientLoginResultMapper());
    }

    private ClientProductFilterInput sampleClientProductFilter() {
        IpAddressBaseFilter assignedIpAddresses = new IpAddressBaseFilter(
                new StringRegExpExactFilter("192.*", "192.168.0.1", null),
                com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType.IPV6,
                null, null, null);
        ClientProductFilterInput nestedAnd = new ClientProductFilterInput(null, null, null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D11), null, null, null, null, null);
        ClientProductFilterInput nestedOr = new ClientProductFilterInput(null, null, null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D12), null, null, null, null, null);
        ClientProductFilterInput nestedNot = new ClientProductFilterInput(null, null, null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D13), null, null, null, null, null);
        return new ClientProductFilterInput(
                SampleFilters.client(),
                SampleFilters.order(),
                SampleFilters.product(),
                SampleFilters.domainName(),
                SampleFilters.ipAddress(),
                assignedIpAddresses,
                SampleFilters.dateTime(SampleFilters.D01),
                SampleFilters.dateTime(SampleFilters.D02),
                SampleFilters.dateTime(SampleFilters.D03),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);
    }

    private ClientGroupFilterInput sampleClientGroupFilter() {
        ClientGroupFilterInput nestedAnd = new ClientGroupFilterInput(
                SampleFilters.dateTime(SampleFilters.D11), null, null, null);
        ClientGroupFilterInput nestedOr = new ClientGroupFilterInput(
                SampleFilters.dateTime(SampleFilters.D12), null, null, null);
        ClientGroupFilterInput nestedNot = new ClientGroupFilterInput(
                SampleFilters.dateTime(SampleFilters.D13), null, null, null);
        return new ClientGroupFilterInput(
                SampleFilters.dateTime(SampleFilters.D02),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);
    }

    private ClientLoginFilterInput sampleClientLoginFilter() {
        ClientLoginFilterInput nestedAnd = new ClientLoginFilterInput(null, null,
                SampleFilters.dateTime(SampleFilters.D11), null, null, null);
        ClientLoginFilterInput nestedOr = new ClientLoginFilterInput(null, null,
                SampleFilters.dateTime(SampleFilters.D12), null, null, null);
        ClientLoginFilterInput nestedNot = new ClientLoginFilterInput(null, null,
                SampleFilters.dateTime(SampleFilters.D13), null, null, null);
        return new ClientLoginFilterInput(
                SampleFilters.client(),
                SampleFilters.ipAddress(),
                SampleFilters.dateTime(SampleFilters.D10),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);
    }

    @Test
    void clients_mapsFilterAndPassesPaginationArgumentsInOrder() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client();
        model.setId(1L);
        model.setFullName("Ivanov Ivan");
        model.setFirstName("Ivan");
        when(clientService.searchClients(any(), any(), any(), any()))
                .thenReturn(Mono.just(new ClientSearchResult("cur-8", true, List.of(model))));

        StepVerifier.create(resolver.clients(SampleFilters.client(), 11, 22, "cur-8"))
                .assertNext(result -> {
                    assertEquals("cur-8", result.cursor());
                    assertTrue(result.hasNextPage());
                    assertEquals(1, result.result().size());
                    Client first = result.result().get(0);
                    assertEquals(1L, first.id());
                    assertEquals("Ivanov Ivan", first.name().fullName());
                    assertEquals("Ivan", first.name().firstName());
                })
                .verifyComplete();

        ArgumentCaptor<ClientFilter> filterCaptor = ArgumentCaptor.forClass(ClientFilter.class);
        verify(clientService).searchClients(filterCaptor.capture(), eq("cur-8"), eq(11), eq(22));
        ClientFilter filter = filterCaptor.getValue();
        assertEquals("exact-eq", filter.getName().getFirstName().getEq());
        assertEquals("exact-eq", filter.getUserOwner().getName().getFirstName().getEq());
        assertEquals("eq@mail.com", filter.getUserOwner().getEmail().getEmail().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), filter.getClientGroup().getUpdatedAt().getUtc().getEq());
        assertEquals("Grunvald", filter.getOrganizationInfo().getNameFull().getEq());
        assertEquals("inn-eq", filter.getInn().getInn().getEq());
        assertEquals("eq@mail.com", filter.getEmail().getEmail().getEq());
        assertEquals("79990001122", filter.getMsisdn().getMsisdn().getEq());
        assertEquals("re.*", filter.getAddress().getAddress().getRegexp());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), filter.getCreatedAt().getUtc().getEq());
        assertEquals(1, filter.getAnd().size());
        assertEquals(1, filter.getOr().size());
        assertNotNull(filter.getNot());
    }

    @Test
    void clients_passesNullArgumentsThrough() {
        when(clientService.searchClients(any(), any(), any(), any()))
                .thenReturn(Mono.just(new ClientSearchResult(null, false, List.of())));

        StepVerifier.create(resolver.clients(null, null, null, null))
                .assertNext(result -> {
                    assertNull(result.cursor());
                    assertFalse(result.hasNextPage());
                    assertEquals(0, result.result().size());
                })
                .verifyComplete();

        verify(clientService).searchClients(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void clientProducts_mapsFilterAndPassesPaginationArgumentsInOrder() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct();
        model.setId(77L);
        model.setClientId(5L);
        model.setOrderId(7L);
        model.setProductId(6L);
        model.setName("VPS");
        when(clientService.searchClientProducts(any(), any(), any(), any()))
                .thenReturn(Mono.just(new ClientProductSearchResult("cur-9", true, List.of(model))));

        StepVerifier.create(resolver.clientProducts(sampleClientProductFilter(), 3, 33, "cur-9"))
                .assertNext(result -> {
                    assertEquals("cur-9", result.cursor());
                    assertTrue(result.hasNextPage());
                    assertEquals(1, result.result().size());
                    ClientProduct first = result.result().get(0);
                    assertEquals("77", first.id());
                    assertEquals(5L, first.clientId());
                    assertEquals(7L, first.orderId());
                    assertEquals(6L, first.productId());
                    assertEquals("VPS", first.name());
                })
                .verifyComplete();

        ArgumentCaptor<ClientProductFilter> filterCaptor = ArgumentCaptor.forClass(ClientProductFilter.class);
        verify(clientService).searchClientProducts(filterCaptor.capture(), eq("cur-9"), eq(3), eq(33));
        ClientProductFilter filter = filterCaptor.getValue();
        assertNotNull(filter.getClient());
        assertEquals("exact-eq", filter.getClient().getName().getFirstName().getEq());
        assertNotNull(filter.getOrder());
        assertEquals(OffsetDateTime.parse(SampleFilters.D08), filter.getOrder().getDate().getUtc().getEq());
        assertNotNull(filter.getProduct());
        assertEquals(OffsetDateTime.parse(SampleFilters.D09), filter.getProduct().getUpdatedAt().getUtc().getEq());
        assertEquals("a.ru", filter.getDomain().getDomainName().getEq());
        assertEquals("10.0.0.1", filter.getDedicatedIpAddress().getIp().getEq());
        assertEquals("192.168.0.1", filter.getAssignedIpAddresses().getIp().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), filter.getRegDate().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), filter.getNextDueDate().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D03), filter.getLastUpdate().getUtc().getEq());
        assertEquals(1, filter.getAnd().size());
        assertEquals(1, filter.getOr().size());
        assertNotNull(filter.getNot());
    }

    @Test
    void clientProducts_passesNullArgumentsThrough() {
        when(clientService.searchClientProducts(any(), any(), any(), any()))
                .thenReturn(Mono.just(new ClientProductSearchResult(null, false, List.of())));

        StepVerifier.create(resolver.clientProducts(null, null, null, null))
                .assertNext(result -> {
                    assertNull(result.cursor());
                    assertFalse(result.hasNextPage());
                    assertEquals(0, result.result().size());
                })
                .verifyComplete();

        verify(clientService).searchClientProducts(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void clientGroups_mapsFilterAndPassesPaginationArgumentsInOrder() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup("Premium", null);
        model.setId(10L);
        when(clientService.searchClientGroups(any(), any(), any(), any()))
                .thenReturn(Mono.just(new ClientGroupSearchResult("cur-10", true, List.of(model))));

        StepVerifier.create(resolver.clientGroups(sampleClientGroupFilter(), 4, 44, "cur-10"))
                .assertNext(result -> {
                    assertEquals("cur-10", result.cursor());
                    assertTrue(result.hasNextPage());
                    assertEquals(1, result.result().size());
                    ClientGroup first = result.result().get(0);
                    assertEquals(10L, first.id());
                    assertEquals("Premium", first.name());
                })
                .verifyComplete();

        ArgumentCaptor<ClientGroupFilter> filterCaptor = ArgumentCaptor.forClass(ClientGroupFilter.class);
        verify(clientService).searchClientGroups(filterCaptor.capture(), eq("cur-10"), eq(4), eq(44));
        ClientGroupFilter filter = filterCaptor.getValue();
        assertNotNull(filter.getUpdatedAt());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), filter.getUpdatedAt().getUtc().getEq());
        assertEquals(1, filter.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), filter.getAnd().get(0).getUpdatedAt().getUtc().getEq());
        assertEquals(1, filter.getOr().size());
        assertNotNull(filter.getNot());
    }

    @Test
    void clientGroups_passesNullArgumentsThrough() {
        when(clientService.searchClientGroups(any(), any(), any(), any()))
                .thenReturn(Mono.just(new ClientGroupSearchResult(null, false, List.of())));

        StepVerifier.create(resolver.clientGroups(null, null, null, null))
                .assertNext(result -> {
                    assertNull(result.cursor());
                    assertFalse(result.hasNextPage());
                    assertEquals(0, result.result().size());
                })
                .verifyComplete();

        verify(clientService).searchClientGroups(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void clientLogins_mapsFilterAndPassesPaginationArgumentsInOrder() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin(1L, null, null);
        model.setId(9L);
        when(clientService.searchClientLogins(any(), any(), any(), any()))
                .thenReturn(Mono.just(new ClientLoginSearchResult("cur-11", true, List.of(model))));

        StepVerifier.create(resolver.clientLogins(sampleClientLoginFilter(), 5, 55, "cur-11"))
                .assertNext(result -> {
                    assertEquals("cur-11", result.cursor());
                    assertTrue(result.hasNextPage());
                    assertEquals(1, result.result().size());
                    ClientLogin first = result.result().get(0);
                    assertEquals(9L, first.id());
                    assertEquals(1L, first.clientId());
                })
                .verifyComplete();

        ArgumentCaptor<ClientLoginFilter> filterCaptor = ArgumentCaptor.forClass(ClientLoginFilter.class);
        verify(clientService).searchClientLogins(filterCaptor.capture(), eq("cur-11"), eq(5), eq(55));
        ClientLoginFilter filter = filterCaptor.getValue();
        assertNotNull(filter.getClient());
        assertEquals("exact-eq", filter.getClient().getName().getFirstName().getEq());
        assertEquals("10.0.0.1", filter.getIpAddress().getIp().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D10), filter.getDate().getUtc().getEq());
        assertEquals(1, filter.getAnd().size());
        assertEquals(1, filter.getOr().size());
        assertNotNull(filter.getNot());
    }

    @Test
    void clientLogins_passesNullArgumentsThrough() {
        when(clientService.searchClientLogins(any(), any(), any(), any()))
                .thenReturn(Mono.just(new ClientLoginSearchResult(null, false, List.of())));

        StepVerifier.create(resolver.clientLogins(null, null, null, null))
                .assertNext(result -> {
                    assertNull(result.cursor());
                    assertFalse(result.hasNextPage());
                    assertEquals(0, result.result().size());
                })
                .verifyComplete();

        verify(clientService).searchClientLogins(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void clients_propagatesServiceError() {
        when(clientService.searchClients(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("client boom")));

        StepVerifier.create(resolver.clients(SampleFilters.client(), 0, 10, null))
                .expectErrorSatisfies(e -> assertEquals("client boom", e.getMessage()))
                .verify();
    }

    @Test
    void clientProducts_emptyServiceResultCompletesEmpty() {
        when(clientService.searchClientProducts(any(), any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(resolver.clientProducts(sampleClientProductFilter(), 0, 10, null))
                .verifyComplete();
    }

    @Test
    void getProductForClientProduct_loadsProductByProductId() {
        Product product = new Product(6L, null, null, null, null, null, null, null);
        when(productDataLoader.load(6L)).thenReturn(CompletableFuture.completedFuture(product));

        ClientProduct clientProduct = new ClientProduct("77", 5L, 7L, 6L, null, null, null, null, null, null,
                null, null, null, null, null, null);

        StepVerifier.create(resolver.getProductForClientProduct(clientProduct, productDataLoader))
                .expectNext(product)
                .verifyComplete();

        verify(productDataLoader).load(6L);
    }

    @Test
    void getProductForClientProduct_nullResultCompletesEmpty() {
        when(productDataLoader.load(6L)).thenReturn(CompletableFuture.completedFuture(null));

        ClientProduct clientProduct = new ClientProduct("77", 5L, 7L, 6L, null, null, null, null, null, null,
                null, null, null, null, null, null);

        StepVerifier.create(resolver.getProductForClientProduct(clientProduct, productDataLoader))
                .verifyComplete();
    }

    @Test
    void getOrderForClientProduct_loadsOrderByOrderId() {
        Order order = new Order(7L, null, null, null, null, null, null, null, null, null, null, null);
        when(orderDataLoader.load(7L)).thenReturn(CompletableFuture.completedFuture(order));

        ClientProduct clientProduct = new ClientProduct("77", 5L, 7L, 6L, null, null, null, null, null, null,
                null, null, null, null, null, null);

        StepVerifier.create(resolver.getOrderForClientProduct(clientProduct, orderDataLoader))
                .expectNext(order)
                .verifyComplete();

        verify(orderDataLoader).load(7L);
    }

    @Test
    void getClientForClientProduct_loadsClientByClientId() {
        Client client = new Client(5L, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
        when(clientDataLoader.load(5L)).thenReturn(CompletableFuture.completedFuture(client));

        ClientProduct clientProduct = new ClientProduct("77", 5L, 7L, 6L, null, null, null, null, null, null,
                null, null, null, null, null, null);

        StepVerifier.create(resolver.getClientForClientProduct(clientProduct, clientDataLoader))
                .expectNext(client)
                .verifyComplete();

        verify(clientDataLoader).load(5L);
    }

    @Test
    void getUserOwnerForClient_loadsUserByOwnerUserId() {
        User user = new User(8L, null, null, null, null);
        when(userDataLoader.load(8L)).thenReturn(CompletableFuture.completedFuture(user));

        Client client = new Client(1L, 8L, 9L, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getUserOwnerForClient(client, userDataLoader))
                .expectNext(user)
                .verifyComplete();

        verify(userDataLoader).load(8L);
    }

    @Test
    void getUserOwnerForClient_nullResultCompletesEmpty() {
        when(userDataLoader.load(8L)).thenReturn(CompletableFuture.completedFuture(null));

        Client client = new Client(1L, 8L, 9L, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getUserOwnerForClient(client, userDataLoader))
                .verifyComplete();
    }

    @Test
    void getClientGroupForClient_loadsGroupByClientGroupId() {
        ClientGroup group = new ClientGroup(9L, null, null, null, null);
        when(clientGroupDataLoader.load(9L)).thenReturn(CompletableFuture.completedFuture(group));

        Client client = new Client(1L, 8L, 9L, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getClientGroupForClient(client, clientGroupDataLoader))
                .expectNext(group)
                .verifyComplete();

        verify(clientGroupDataLoader).load(9L);
    }

    @Test
    void getClientGroupForClient_nullResultCompletesEmpty() {
        when(clientGroupDataLoader.load(9L)).thenReturn(CompletableFuture.completedFuture(null));

        Client client = new Client(1L, 8L, 9L, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getClientGroupForClient(client, clientGroupDataLoader))
                .verifyComplete();
    }

    @Test
    void getClientForClientLogin_loadsClientByClientId() {
        Client client = new Client(1L, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
        when(clientDataLoader.load(1L)).thenReturn(CompletableFuture.completedFuture(client));

        ClientLogin clientLogin = new ClientLogin(9L, 1L, null, null);

        StepVerifier.create(resolver.getClientForClientLogin(clientLogin, clientDataLoader))
                .expectNext(client)
                .verifyComplete();

        verify(clientDataLoader).load(1L);
    }

    @Test
    void getClientForClientLogin_nullResultCompletesEmpty() {
        when(clientDataLoader.load(1L)).thenReturn(CompletableFuture.completedFuture(null));

        ClientLogin clientLogin = new ClientLogin(9L, 1L, null, null);

        StepVerifier.create(resolver.getClientForClientLogin(clientLogin, clientDataLoader))
                .verifyComplete();
    }
}
