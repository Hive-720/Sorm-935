package com.kjsoft.sorm.infrastructure.whmcsprovider;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.WhmcsApiClient;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.request.*;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.*;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper.*;
import com.kjsoft.sorm.service.whmcsprovider.dto.ProductProviderDto;
import com.kjsoft.sorm.service.whmcsprovider.WhmcsProvider;
import com.kjsoft.sorm.service.whmcsprovider.dto.ClientGroupProviderDto;
import com.kjsoft.sorm.service.whmcsprovider.dto.CreditProviderDto;
import com.kjsoft.sorm.service.whmcsprovider.dto.InvoiceAdditionalDataProviderDto;
import com.kjsoft.sorm.service.whmcsprovider.dto.PaymentMethodProviderDto;
import com.kjsoft.sorm.service.whmcsprovider.result.ActivityLogsResult;
import com.kjsoft.sorm.service.whmcsprovider.ProviderException;
import com.kjsoft.sorm.service.whmcsprovider.result.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;


@Component
@RequiredArgsConstructor
@ConditionalOnBean(WhmcsApiClient.class)
public class WhmcsProviderImpl implements WhmcsProvider {
    private final WhmcsApiClient apiClient;
    private final ActivityLogProviderMapper activityLogMapper;
    private final AffiliateProviderMapper affiliateMapper;
    private final ClientGroupProviderMapper groupMapper;
    private final ClientProductProviderMapper clientProductMapper;
    private final ClientProviderMapper clientMapper;
    private final ClientShortProviderMapper clientShortMapper;
    private final ContactProviderMapper contactMapper;
    private final CreditProviderMapper creditMapper;
    private final EmailProviderMapper emailMapper;
    private final InvoiceProviderMapper invoiceMapper;
    private final OrderProviderMapper orderMapper;
    private final PaymentMethodProviderMapper paymentMethodMapper;
    private final ProductProviderMapper productMapper;
    private final UserProviderMapper userMapper;
    private final TransactionProviderMapper transactionMapper;
    private final InvoiceItemProviderMapper invoiceItemMapper;


    @Override
    public Mono<ActivityLogsResult> getActivityLogs(long offset, int limit, OffsetDateTime before) {
        return apiClient.request(new GetActivityLogRequest(offset, limit))
                .flatMap(res -> Flux.fromIterable(res.getActivities())
                        .filter(activityLog -> before.isBefore(activityLog.getDate()))
                        .map(activityLogMapper::map)
                        .collectList()
                        .map(list -> ActivityLogsResult.builder()
                                .elements(list)
                                .hashNextPage(hashNextPage(list.size(), limit, res.getTotalCount()))
                                .nextOffset(offset + list.size())
                                .build()
                        )
                );
    }

    @Override
    public Mono<AffiliatesResult> getAffiliates(long offset, int limit) {
        return apiClient.request(new GetAffiliatesRequest(offset, limit))
                .flatMap(res -> Flux.fromIterable(res.getAffiliates())
                        .map(affiliateMapper::map)
                        .collectList()
                        .map(list -> AffiliatesResult.builder()
                                .elements(list)
                                .hasNextPage(hashNextPage(list.size(), limit, res.getTotalCount()))
                                .nextOffset(offset + list.size())
                                .build()
                        )
                );
    }

    @Override
    public Mono<ClientsResult> getClients(long offset, int limit) {
        return apiClient.request(new GetClientsRequest(offset, limit))
                .flatMap(res -> Flux.fromIterable(res.getClients())
                        .map(clientShortMapper::map)
                        .collectList()
                        .map(list -> ClientsResult.builder()
                                .elements(list)
                                .hasNextPage(hashNextPage(list.size(), limit, res.getTotalCount()))
                                .nextOffset(offset + list.size())
                                .build()
                        )
                );
    }

    @Override
    public Mono<ClientDetailsResult> getClientDetails(long clientId) {
        return apiClient.request(new GetClientDetailsRequest(clientId))
                .map(clientMapper::map)
                .map(client -> ClientDetailsResult.builder()
                        .element(client)
                        .build()
                )
                .onErrorResume(ProviderException.class, e -> {
                    if (e.isNotFoundError()) {
                        return Mono.just(ClientDetailsResult.builder()
                                .deleted(true)
                                .build()
                        );
                    } else {
                        return Mono.error(e);
                    }
                });
    }

    @Override
    public Flux<ClientGroupProviderDto> getClientGroups() {
        return apiClient.request(new GetClientGroupRequest())
                .map(GetClientGroupsResponse::getClientCroups)
                .flatMapMany(Flux::fromIterable)
                .map(groupMapper::map);
    }

    @Override
    public Mono<ClientProductsResult> getClientProducts(long offset, int limit) {
        return apiClient.request(new GetClientProductsRequest(offset, limit))
                .flatMap(res -> Flux.fromIterable(res.getClientProducts())
                        .map(clientProductMapper::map)
                        .collectList()
                        .map(list -> ClientProductsResult.builder()
                                .elements(list)
                                .hasNextPage(hashNextPage(list.size(), limit, res.getTotalCount()))
                                .nextOffset(offset + list.size())
                                .build()
                        )
                );
    }

    @Override
    public Mono<OrdersResult> getOrders(long offset, int limit, OffsetDateTime before) {
        return apiClient.request(new GetOrdersRequest(offset, limit))
                .flatMap(res -> Flux.fromIterable(res.getOrders())
                        .filter(order -> before.isBefore(order.getDate()))
                        .map(orderMapper::map)
                        .collectList()
                        .map(list -> OrdersResult.builder()
                                .elements(list)
                                .hasNextPage(hashNextPage(list.size(), limit, res.getTotalCount()))
                                .nextOffset(offset + list.size())
                                .build()
                        )
                );
    }

    @Override
    public Mono<ContactsResult> getContacts(long offset, int limit) {
        return apiClient.request(new GetContactRequest(offset, limit))
                .flatMap(response -> Flux.fromIterable(response.getContacts())
                        .map(contactMapper::map)
                        .collectList()
                        .map(list -> ContactsResult.builder()
                                .elements(list)
                                .hasNextPage(hashNextPage(list.size(), limit, response.getTotalCount()))
                                .nextOffset(offset + list.size())
                                .build()
                        )
                );
    }

    @Override
    public Flux<CreditProviderDto> getCredits(long clientId, OffsetDateTime before) {
        return apiClient.request(new GetCreditsRequest(clientId))
                .onErrorResume(ProviderException.class, e -> e.isNotFoundError() ? Mono.empty() : Mono.error(e))
                .map(GetCreditsResponse::getCredits)
                .flatMapMany(Flux::fromIterable)
                .filter(credit -> before.isBefore(credit.getDate()))
                .map(creditMapper::map);
    }

    @Override
    public Mono<EmailsResult> getEmails(long clientId, long offset, int limit, OffsetDateTime before) {
        return apiClient.request(new GetEmailsRequest(clientId, offset, limit))
                .onErrorResume(ProviderException.class, e -> e.isNotFoundError() ? Mono.empty() : Mono.error(e))
                .flatMap(res -> Flux.fromIterable(res.getEmails())
                        .filter(email -> before.isBefore(email.getDate()))
                        .map(emailMapper::map)
                        .collectList()
                        .map(list -> EmailsResult.builder()
                                .elements(list)
                                .hasNextPage(hashNextPage(list.size(), limit, res.getTotalCount()))
                                .nextOffset(offset + list.size())
                                .build()
                        )
                );
    }

    @Override
    public Flux<PaymentMethodProviderDto> getPaymentMethods() {
        return apiClient.request(new GetPaymentMethodRequest())
                .map(GetPaymentMethodResponse::getPaymentMethods)
                .flatMapMany(Flux::fromIterable)
                .map(paymentMethodMapper::map);
    }

    @Override
    public Mono<UsersResult> getUsers(long offset, int limit) {
        return apiClient.request(new GetUsersRequest(offset, limit))
                .flatMap(res -> Flux.fromIterable(res.getUsers())
                        .map(userMapper::map)
                        .collectList()
                        .map(list -> UsersResult.builder()
                                .elements(list)
                                .hashNextPage(hashNextPage(list.size(), limit, res.getTotalCount()))
                                .nextOffset(offset + list.size())
                                .build()
                        )
                );
    }

    @Override
    public Flux<ProductProviderDto> getProducts() {
        return apiClient.request(new GetProductsRequest())
                .map(GetProductsResponse::getProducts)
                .flatMapMany(Flux::fromIterable)
                .map(productMapper::map);
    }

    @Override
    public Mono<InvoicesResult> getInvoices(long offset, int limit, OffsetDateTime before) {
        return apiClient.request(new GetInvoicesRequest(offset, limit))
                .flatMap(res -> Flux.fromIterable(res.getInvoices())
                        .filter(invoice -> before.isBefore(invoice.getDueDate()))
                        .map(invoiceMapper::map)
                        .collectList()
                        .map(list -> InvoicesResult.builder()
                                .elements(list)
                                .hasNextPage(hashNextPage(list.size(), limit, res.getTotalCount()))
                                .nextOffset(offset + list.size())
                                .build()
                        )
                );
    }

    @Override
    public Mono<InvoiceAdditionalDataProviderDto> getInvoiceAdditionalData(long invoiceId) {
        return apiClient.request(new GetInvoiceRequest(invoiceId))
                .map(res -> InvoiceAdditionalDataProviderDto.builder()
                        .id(res.getId())
                        .balance(res.getBalance())
                        .items(mapToItemDtoList(res.getItems()))
                        .transactions(mapToTrxDtoList(res.getTransactions()))
                        .build()
                );
    }

    private List<InvoiceAdditionalDataProviderDto.TransactionDto> mapToTrxDtoList(List<GetInvoiceResponse.Transactions.Transaction> transactions) {
        return transactions.stream()
                .map(transactionMapper::map)
                .toList();
    }

    private List<InvoiceAdditionalDataProviderDto.Item> mapToItemDtoList(List<GetInvoiceResponse.Items.Item> items) {
        return items.stream()
                .map(invoiceItemMapper::map)
                .toList();
    }

    private boolean hashNextPage(int size, int limit, long totalCount) {
        return size < limit || (size + limit) >= totalCount;
    }
}