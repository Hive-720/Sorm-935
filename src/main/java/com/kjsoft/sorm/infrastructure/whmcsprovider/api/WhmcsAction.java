package com.kjsoft.sorm.infrastructure.whmcsprovider.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum WhmcsAction {
    GET_ACTIVITY_LOG("GetActivityLog"),
    GET_AFFILIATES("GetAffiliates"),
    GET_CLIENT_DETAILS("GetClientsDetails"),
    GET_CLIENT_GROUPS("GetClientGroups"),
    GET_CLIENT_PRODUCTS("GetClientsProducts"),
    GET_CLIENTS("GetClients"),
    GET_CONTACTS("GetContacts"),
    GET_CREDITS("GetCredits"),
    GET_EMAILS("GetEmails"),
    GET_INVOICE("GetInvoice"),
    GET_INVOICES("GetInvoices"),
    GET_ORDERS("GetOrders"),
    GET_PAYMENT_METHOD("GetPaymentMethods"),
    GET_PRODUCTS("GetProducts"),
    GET_USERS("GetUsers");

    private final String nameAction;
}