package com.kjsoft.sorm.service.whmcsdata.dataservice.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

import static com.kjsoft.sorm.util.EqualsUtils.isBigDecimalEqual;


@Getter
@Setter
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long relId;
    private String type;
    private String productType;
    private String productName;
    private String domain;
    private BigDecimal billingCycle;
    private BigDecimal amount;
    private String status;


    public boolean update(OrderItem actual) {
        var changed = false;

        if (!Objects.equals(this.type, actual.type)) {
            this.type = actual.type;
            changed = true;
        }

        if (!Objects.equals(this.productType, actual.productType)) {
            this.productType = actual.productType;
            changed = true;
        }

        if (!Objects.equals(this.productName, actual.productName)) {
            this.productName = actual.productName;
            changed = true;
        }

        if (!Objects.equals(this.domain, actual.domain)) {
            this.domain = actual.domain;
            changed = true;
        }

        if (!Objects.equals(this.billingCycle, actual.billingCycle)) {
            this.billingCycle = actual.billingCycle;
            changed = true;
        }

        if (!isBigDecimalEqual(amount, actual.amount)) {
            this.amount = actual.amount;
            changed = true;
        }

        if (!Objects.equals(this.status, actual.status)) {
            this.status = actual.status;
            changed = true;
        }

        return changed;
    }
}