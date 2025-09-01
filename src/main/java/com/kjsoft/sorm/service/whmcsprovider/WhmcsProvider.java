package com.kjsoft.sorm.service.whmcsprovider;

import com.kjsoft.sorm.service.whmcsprovider.dto.ProductProviderDto;
import com.kjsoft.sorm.service.whmcsprovider.dto.ClientGroupProviderDto;
import com.kjsoft.sorm.service.whmcsprovider.dto.CreditProviderDto;
import com.kjsoft.sorm.service.whmcsprovider.dto.InvoiceAdditionalDataProviderDto;
import com.kjsoft.sorm.service.whmcsprovider.dto.PaymentMethodProviderDto;
import com.kjsoft.sorm.service.whmcsprovider.result.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;


public interface WhmcsProvider {

    Mono<ActivityLogsResult> getActivityLogs(long offset, int limit, OffsetDateTime before);

    Mono<AffiliatesResult> getAffiliates(long offset, int limit);

    Mono<ClientsResult> getClients(long offset, int limit);

    Mono<ClientDetailsResult> getClientDetails(long clientId);

    Flux<ClientGroupProviderDto> getClientGroups();

    Mono<ClientProductsResult> getClientProducts(long offset, int limit);

    Mono<OrdersResult> getOrders(long offset, int limit, OffsetDateTime before);

    Mono<ContactsResult> getContacts(long offset, int limit);

    Flux<CreditProviderDto> getCredits(long clientId, OffsetDateTime before);

    Mono<EmailsResult> getEmails(long clientId, long offset, int limit, OffsetDateTime before);

    Flux<PaymentMethodProviderDto> getPaymentMethods();

    Mono<UsersResult> getUsers(long offset, int limit);

    Flux<ProductProviderDto> getProducts();

    Mono<InvoicesResult> getInvoices(long offset, int limit, OffsetDateTime before);

    Mono<InvoiceAdditionalDataProviderDto> getInvoiceAdditionalData(long invoiceId);

}