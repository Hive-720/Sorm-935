package com.kjsoft.sorm.syncdatascheduler.scheduler;

import com.kjsoft.sorm.syncdatascheduler.scheduler.impl.*;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum SchedulerType {
    ACTIVITY_LOG("ActivityLog", ActivityLogSyncScheduler.class),
    AFFILIATE("Affiliate", AffiliateSyncScheduler.class),
    CLIENT_DETAIL("ClientDetails", ClientDetailSyncScheduler.class),
    CLIENT_GROUP("ClientGroup", ClientGroupSyncScheduler.class),
    CLIENT_PRODUCT("ClientProduct", ClientProductSyncScheduler.class),
    CLIENT("Client", ClientSyncScheduler.class),
    CONTACT("Contact", ContactSyncScheduler.class),
    CONTACT_DELETE("DeleteContact", ContactDeleteSyncScheduler.class),
    CREDIT("Credit", CreditSyncScheduler.class),
    EMAIL("Email", EmailSyncScheduler.class),
    INVOICE("Invoice", InvoiceSyncScheduler.class),
    ORDER("Order", OrderSyncScheduler.class),
    PAYMENT_METHOD("PaymentMethod", PaymentMethodSyncScheduler.class),
    PRODUCT("Product", ProductSyncScheduler.class),
    USER("User", UserSyncScheduler.class);

    private final String commonName;
    private final Class<? extends Scheduler> clazz;

}