package com.grunvald.sorm.service.whmcsdata.dataservice.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

import static com.grunvald.sorm.util.EqualsUtils.isBigDecimalEqual;
import static com.grunvald.sorm.util.EqualsUtils.isOffsetDateTimeEqual;


@Getter
@Setter
public class Order {
    private Long id;
    private Long clientId;
    private Long contactId;
    private Long invoiceId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String ipAddress;
    private String paymentMethod;
    private String paymentMethodName;
    private String paymentStatus;
    private OffsetDateTime date;


    public boolean update(Order actual) {
        var changed = false;

        if (!isBigDecimalEqual(amount, actual.amount)) {
            this.amount = actual.amount;
            changed = true;
        }

        if (!Objects.equals(this.currency, actual.currency)) {
            this.currency = actual.currency;
            changed = true;
        }

        if (!Objects.equals(this.status, actual.status)) {
            this.status = actual.status;
            changed = true;
        }

        if (!Objects.equals(this.paymentMethod, actual.paymentMethod)) {
            this.paymentMethod = actual.paymentMethod;
            changed = true;
        }

        if (!Objects.equals(this.paymentMethodName, actual.paymentMethodName)) {
            this.paymentMethodName = actual.paymentMethodName;
            changed = true;
        }

        if (!Objects.equals(this.paymentStatus, actual.paymentStatus)) {
            this.paymentStatus = actual.paymentStatus;
            changed = true;
        }

        if (!isOffsetDateTimeEqual(this.date, actual.date)) {
            this.date = actual.date;
            changed = true;
        }

        return changed;
    }
}