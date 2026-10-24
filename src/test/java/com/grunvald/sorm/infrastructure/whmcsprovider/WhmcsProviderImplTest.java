package com.grunvald.sorm.infrastructure.whmcsprovider;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsAction;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.WhmcsApiClient;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.request.Request;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetActivityLogResponse;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetAffiliatesResponse;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientGroupsResponse;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientProductsResponse;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientsDetailsResponse;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientsResponse;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetContactsResponse;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetCreditsResponse;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetEmailsResponse;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetInvoiceResponse;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetInvoicesResponse;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetOrdersResponse;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetPaymentMethodResponse;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetProductsResponse;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetUsersResponse;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.ActivityLogProviderMapper;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.AffiliateProviderMapper;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.ClientGroupProviderMapper;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.ClientProductProviderMapper;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.ClientProviderMapper;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.ClientShortProviderMapper;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.ContactProviderMapper;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.CreditProviderMapper;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.EmailProviderMapper;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.InvoiceItemProviderMapper;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.InvoiceProviderMapper;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.OrderProviderMapper;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.PaymentMethodProviderMapper;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.ProductProviderMapper;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.TransactionProviderMapper;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper.UserProviderMapper;
import com.grunvald.sorm.service.whmcsprovider.ProviderException;
import com.grunvald.sorm.service.whmcsprovider.dto.ActivityLogProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.AffiliateProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientGroupProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientProductProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientShortProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.ContactProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.CreditProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.EmailProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceAdditionalDataProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.OrderProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.PaymentMethodProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.ProductProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.UserProviderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static com.grunvald.sorm.service.whmcsprovider.ProviderException.Error.EXTERNAL_ERROR;
import static com.grunvald.sorm.service.whmcsprovider.ProviderException.Error.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class WhmcsProviderImplTest {

    private static final OffsetDateTime BEFORE = OffsetDateTime.parse("2026-01-01T00:00:00Z");
    private static final OffsetDateTime INSIDE = OffsetDateTime.parse("2026-02-01T00:00:00Z");
    private static final OffsetDateTime OUTSIDE = OffsetDateTime.parse("2025-12-01T00:00:00Z");

    private WhmcsApiClient apiClient;
    private ActivityLogProviderMapper activityLogMapper;
    private AffiliateProviderMapper affiliateMapper;
    private ClientGroupProviderMapper groupMapper;
    private ClientProductProviderMapper clientProductMapper;
    private ClientProviderMapper clientMapper;
    private ClientShortProviderMapper clientShortMapper;
    private ContactProviderMapper contactMapper;
    private CreditProviderMapper creditMapper;
    private EmailProviderMapper emailMapper;
    private InvoiceProviderMapper invoiceMapper;
    private OrderProviderMapper orderMapper;
    private PaymentMethodProviderMapper paymentMethodMapper;
    private ProductProviderMapper productMapper;
    private UserProviderMapper userMapper;
    private TransactionProviderMapper transactionMapper;
    private InvoiceItemProviderMapper invoiceItemMapper;
    private WhmcsProviderImpl provider;

    @BeforeEach
    void setUp() {
        apiClient = mock(WhmcsApiClient.class);
        activityLogMapper = mock(ActivityLogProviderMapper.class);
        affiliateMapper = mock(AffiliateProviderMapper.class);
        groupMapper = mock(ClientGroupProviderMapper.class);
        clientProductMapper = mock(ClientProductProviderMapper.class);
        clientMapper = mock(ClientProviderMapper.class);
        clientShortMapper = mock(ClientShortProviderMapper.class);
        contactMapper = mock(ContactProviderMapper.class);
        creditMapper = mock(CreditProviderMapper.class);
        emailMapper = mock(EmailProviderMapper.class);
        invoiceMapper = mock(InvoiceProviderMapper.class);
        orderMapper = mock(OrderProviderMapper.class);
        paymentMethodMapper = mock(PaymentMethodProviderMapper.class);
        productMapper = mock(ProductProviderMapper.class);
        userMapper = mock(UserProviderMapper.class);
        transactionMapper = mock(TransactionProviderMapper.class);
        invoiceItemMapper = mock(InvoiceItemProviderMapper.class);
        provider = new WhmcsProviderImpl(apiClient, activityLogMapper, affiliateMapper, groupMapper,
                clientProductMapper, clientMapper, clientShortMapper, contactMapper, creditMapper,
                emailMapper, invoiceMapper, orderMapper, paymentMethodMapper, productMapper,
                userMapper, transactionMapper, invoiceItemMapper);
    }

    @Test
    void getActivityLogs_filtersByBeforeAndBuildsResult() {
        var keep = activityLog(INSIDE);
        var skip = activityLog(OUTSIDE);
        var response = mock(GetActivityLogResponse.class);
        when(response.getActivities()).thenReturn(List.of(keep, skip));
        when(response.getTotalCount()).thenReturn(5L);
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var dto = mock(ActivityLogProviderDto.class);
        when(activityLogMapper.map(keep)).thenReturn(dto);

        var result = provider.getActivityLogs(1L, 10, BEFORE).block();

        assertEquals(List.of(dto), result.getElements());
        assertTrue(result.isHashNextPage());
        assertEquals(2L, result.getNextOffset());

        var captor = ArgumentCaptor.forClass(Request.class);
        verify(apiClient).request(captor.capture());
        assertEquals(WhmcsAction.GET_ACTIVITY_LOG, captor.getValue().getAction());
        assertEquals(1L, captor.getValue().getOffset());
        assertEquals(10, captor.getValue().getLimit());
    }

    @Test
    void getActivityLogs_lastPage_hasNoNextPage() {
        var keep = activityLog(INSIDE);
        var response = mock(GetActivityLogResponse.class);
        when(response.getActivities()).thenReturn(List.of(keep));
        when(response.getTotalCount()).thenReturn(2L);
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var dto = mock(ActivityLogProviderDto.class);
        when(activityLogMapper.map(keep)).thenReturn(dto);

        var result = provider.getActivityLogs(1L, 10, BEFORE).block();

        assertFalse(result.isHashNextPage());
        assertEquals(2L, result.getNextOffset());
    }

    @Test
    void getClientDetails_success_sendsClientDetailsActionAndMapsElement() {
        var response = mock(GetClientsDetailsResponse.class);
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var dto = mock(ClientProviderDto.class);
        when(clientMapper.map(response)).thenReturn(dto);

        var result = provider.getClientDetails(7L).block();

        assertEquals(dto, result.getElement());
        assertFalse(result.isDeleted());

        var captor = ArgumentCaptor.forClass(Request.class);
        verify(apiClient).request(captor.capture());
        assertEquals(WhmcsAction.GET_CLIENT_DETAILS, captor.getValue().getAction());
    }

    @Test
    void getClientDetails_notFound_returnsDeletedResult() {
        when(apiClient.request(any()))
                .thenReturn(Mono.error(new ProviderException(NOT_FOUND, "client not found")));

        var result = provider.getClientDetails(7L).block();

        assertTrue(result.isDeleted());
        assertNull(result.getElement());
    }

    @Test
    void getClientDetails_otherError_propagates() {
        when(apiClient.request(any()))
                .thenReturn(Mono.error(new ProviderException(EXTERNAL_ERROR, "boom")));

        StepVerifier.create(provider.getClientDetails(7L))
                .expectError(ProviderException.class)
                .verify();
    }

    @Test
    void getCredits_notFound_returnsEmptyFlux() {
        when(apiClient.request(any()))
                .thenReturn(Mono.error(new ProviderException(NOT_FOUND, "client not found")));

        StepVerifier.create(provider.getCredits(1L, BEFORE))
                .verifyComplete();
    }

    @Test
    void getCredits_filtersByBeforeAndMaps() {
        var keep = mock(GetCreditsResponse.Credits.Credit.class);
        when(keep.getDate()).thenReturn(INSIDE);
        var skip = mock(GetCreditsResponse.Credits.Credit.class);
        when(skip.getDate()).thenReturn(OUTSIDE);
        var response = mock(GetCreditsResponse.class);
        when(response.getCredits()).thenReturn(List.of(keep, skip));
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var dto = mock(CreditProviderDto.class);
        when(creditMapper.map(keep)).thenReturn(dto);

        StepVerifier.create(provider.getCredits(1L, BEFORE))
                .expectNext(dto)
                .verifyComplete();

        var captor = ArgumentCaptor.forClass(Request.class);
        verify(apiClient).request(captor.capture());
        assertEquals(WhmcsAction.GET_CREDITS, captor.getValue().getAction());
    }

    @Test
    void getEmails_notFound_returnsEmptyMono() {
        when(apiClient.request(any()))
                .thenReturn(Mono.error(new ProviderException(NOT_FOUND, "client not found")));

        StepVerifier.create(provider.getEmails(1L, 0L, 10, BEFORE))
                .verifyComplete();
    }

    @Test
    void getEmails_filtersByBeforeAndBuildsResult() {
        var keep = mock(GetEmailsResponse.Emails.Email.class);
        when(keep.getDate()).thenReturn(INSIDE);
        var response = mock(GetEmailsResponse.class);
        when(response.getEmails()).thenReturn(List.of(keep));
        when(response.getTotalCount()).thenReturn(1L);
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var dto = mock(EmailProviderDto.class);
        when(emailMapper.map(keep)).thenReturn(dto);

        var result = provider.getEmails(1L, 0L, 10, BEFORE).block();

        assertEquals(List.of(dto), result.getElements());
        assertFalse(result.isHasNextPage());
        assertEquals(1L, result.getNextOffset());
    }

    @Test
    void getClientGroups_mapsAll() {
        var g1 = mock(GetClientGroupsResponse.Groups.Group.class);
        var g2 = mock(GetClientGroupsResponse.Groups.Group.class);
        var response = mock(GetClientGroupsResponse.class);
        when(response.getClientCroups()).thenReturn(List.of(g1, g2));
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var dto1 = mock(ClientGroupProviderDto.class);
        var dto2 = mock(ClientGroupProviderDto.class);
        when(groupMapper.map(g1)).thenReturn(dto1);
        when(groupMapper.map(g2)).thenReturn(dto2);

        StepVerifier.create(provider.getClientGroups())
                .expectNext(dto1, dto2)
                .verifyComplete();
    }

    @Test
    void getPaymentMethods_mapsAll() {
        var pm1 = mock(GetPaymentMethodResponse.PaymentMethod.class);
        var response = mock(GetPaymentMethodResponse.class);
        when(response.getPaymentMethods()).thenReturn(List.of(pm1));
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var dto = mock(PaymentMethodProviderDto.class);
        when(paymentMethodMapper.map(pm1)).thenReturn(dto);

        StepVerifier.create(provider.getPaymentMethods())
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void getProducts_mapsAll() {
        var p1 = mock(GetProductsResponse.Products.Product.class);
        var response = mock(GetProductsResponse.class);
        when(response.getProducts()).thenReturn(List.of(p1));
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var dto = mock(ProductProviderDto.class);
        when(productMapper.map(p1)).thenReturn(dto);

        StepVerifier.create(provider.getProducts())
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void getUsers_mapsAndBuildsResult() {
        var u1 = mock(GetUsersResponse.User.class);
        var response = mock(GetUsersResponse.class);
        when(response.getUsers()).thenReturn(List.of(u1));
        when(response.getTotalCount()).thenReturn(3L);
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var dto = mock(UserProviderDto.class);
        when(userMapper.map(u1)).thenReturn(dto);

        var result = provider.getUsers(0L, 10).block();

        assertEquals(List.of(dto), result.getElements());
        assertTrue(result.isHashNextPage());
        assertEquals(1L, result.getNextOffset());

        var captor = ArgumentCaptor.forClass(Request.class);
        verify(apiClient).request(captor.capture());
        assertEquals(WhmcsAction.GET_USERS, captor.getValue().getAction());
    }

    @Test
    void getOrders_filtersByBeforeAndBuildsResult() {
        var keep = mock(GetOrdersResponse.Orders.Order.class);
        when(keep.getDate()).thenReturn(INSIDE);
        var skip = mock(GetOrdersResponse.Orders.Order.class);
        when(skip.getDate()).thenReturn(OUTSIDE);
        var response = mock(GetOrdersResponse.class);
        when(response.getOrders()).thenReturn(List.of(keep, skip));
        when(response.getTotalCount()).thenReturn(10L);
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var dto = mock(OrderProviderDto.class);
        when(orderMapper.map(keep)).thenReturn(dto);

        var result = provider.getOrders(0L, 10, BEFORE).block();

        assertEquals(List.of(dto), result.getElements());
        assertTrue(result.isHasNextPage());
        assertEquals(1L, result.getNextOffset());
    }

    @Test
    void getInvoices_filtersByDueDateAndBuildsResult() {
        var keep = mock(GetInvoicesResponse.Invoice.class);
        when(keep.getDueDate()).thenReturn(INSIDE);
        var skip = mock(GetInvoicesResponse.Invoice.class);
        when(skip.getDueDate()).thenReturn(OUTSIDE);
        var response = mock(GetInvoicesResponse.class);
        when(response.getInvoices()).thenReturn(List.of(keep, skip));
        when(response.getTotalCount()).thenReturn(1L);
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var dto = mock(InvoiceProviderDto.class);
        when(invoiceMapper.map(keep)).thenReturn(dto);

        var result = provider.getInvoices(0L, 10, BEFORE).block();

        assertEquals(List.of(dto), result.getElements());
        assertFalse(result.isHasNextPage());
        assertEquals(1L, result.getNextOffset());
    }

    @Test
    void getAffiliates_mapsAndBuildsResult() {
        var a1 = mock(GetAffiliatesResponse.Affiliate.class);
        var response = mock(GetAffiliatesResponse.class);
        when(response.getAffiliates()).thenReturn(List.of(a1));
        when(response.getTotalCount()).thenReturn(1L);
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var dto = mock(AffiliateProviderDto.class);
        when(affiliateMapper.map(a1)).thenReturn(dto);

        var result = provider.getAffiliates(0L, 10).block();

        assertEquals(List.of(dto), result.getElements());
        assertFalse(result.isHasNextPage());
    }

    @Test
    void getClients_mapsAndBuildsResult() {
        var c1 = mock(GetClientsResponse.Clients.Client.class);
        var response = mock(GetClientsResponse.class);
        when(response.getClients()).thenReturn(List.of(c1));
        when(response.getTotalCount()).thenReturn(5L);
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var dto = mock(ClientShortProviderDto.class);
        when(clientShortMapper.map(c1)).thenReturn(dto);

        var result = provider.getClients(0L, 10).block();

        assertEquals(List.of(dto), result.getElements());
        assertTrue(result.isHasNextPage());

        var captor = ArgumentCaptor.forClass(Request.class);
        verify(apiClient).request(captor.capture());
        assertEquals(WhmcsAction.GET_CLIENTS, captor.getValue().getAction());
    }

    @Test
    void getClientProducts_mapsAndBuildsResult() {
        var p1 = mock(GetClientProductsResponse.Products.Product.class);
        var response = mock(GetClientProductsResponse.class);
        when(response.getClientProducts()).thenReturn(List.of(p1));
        when(response.getTotalCount()).thenReturn(1L);
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var dto = mock(ClientProductProviderDto.class);
        when(clientProductMapper.map(p1)).thenReturn(dto);

        var result = provider.getClientProducts(2L, 10).block();

        assertEquals(List.of(dto), result.getElements());
        assertFalse(result.isHasNextPage());

        var captor = ArgumentCaptor.forClass(Request.class);
        verify(apiClient).request(captor.capture());
        assertEquals(WhmcsAction.GET_CLIENT_PRODUCTS, captor.getValue().getAction());
        assertEquals(2L, captor.getValue().getOffset());
        assertEquals(10, captor.getValue().getLimit());
    }

    @Test
    void getContacts_mapsAndBuildsResult() {
        var c1 = mock(GetContactsResponse.Contacts.Contact.class);
        var response = mock(GetContactsResponse.class);
        when(response.getContacts()).thenReturn(List.of(c1));
        when(response.getTotalCount()).thenReturn(1L);
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var dto = mock(ContactProviderDto.class);
        when(contactMapper.map(c1)).thenReturn(dto);

        var result = provider.getContacts(0L, 10).block();

        assertEquals(List.of(dto), result.getElements());
        assertFalse(result.isHasNextPage());
    }

    @Test
    void getInvoiceAdditionalData_mapsBalanceItemsAndTransactions() {
        var item = mock(GetInvoiceResponse.Items.Item.class);
        var trx = mock(GetInvoiceResponse.Transactions.Transaction.class);
        var response = mock(GetInvoiceResponse.class);
        when(response.getId()).thenReturn(99L);
        when(response.getBalance()).thenReturn(new BigDecimal("12.50"));
        when(response.getItems()).thenReturn(List.of(item));
        when(response.getTransactions()).thenReturn(List.of(trx));
        when(apiClient.request(any())).thenReturn(Mono.just(response));
        var itemDto = mock(InvoiceAdditionalDataProviderDto.Item.class);
        var trxDto = mock(InvoiceAdditionalDataProviderDto.TransactionDto.class);
        when(invoiceItemMapper.map(item)).thenReturn(itemDto);
        when(transactionMapper.map(trx)).thenReturn(trxDto);

        var result = provider.getInvoiceAdditionalData(99L).block();

        assertEquals(99L, result.getId());
        assertEquals(new BigDecimal("12.50"), result.getBalance());
        assertEquals(List.of(itemDto), result.getItems());
        assertEquals(List.of(trxDto), result.getTransactions());
    }

    private static GetActivityLogResponse.Activity.ActivityLog activityLog(OffsetDateTime date) {
        var log = new GetActivityLogResponse.Activity.ActivityLog();
        log.setDate(date);
        return log;
    }
}
