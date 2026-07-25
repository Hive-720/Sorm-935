package com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

import static com.grunvald.sorm.util.EqualsUtils.isBigDecimalEqual;
import static com.grunvald.sorm.util.EqualsUtils.isOffsetDateTimeEqual;


@Getter
@Setter
public class Affiliate {
    private Long id;
    private Long clientId;
    private Long visitors;
    private String payType;
    private BigDecimal payAmount;
    private Boolean oneTime;
    private BigDecimal balance;
    private BigDecimal withdrawn;
    private OffsetDateTime date;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;


    public boolean update(Affiliate actual) {
        var isChanged = false;

        if (!Objects.equals(this.visitors, actual.visitors)) {
            this.visitors = actual.visitors;
            isChanged = true;
        }

        if (!Objects.equals(this.payType, actual.getPayType())) {
            this.payType = actual.payType;
            isChanged = true;
        }

        if (!isBigDecimalEqual(this.payAmount, actual.payAmount)) {
            this.payAmount = actual.payAmount;
            isChanged = true;
        }

        if (!Objects.equals(this.oneTime, actual.oneTime)) {
            this.oneTime = actual.oneTime;
            isChanged = true;
        }

        if (!isBigDecimalEqual(this.balance, actual.balance)) {
            this.balance = actual.balance;
            isChanged = true;
        }

        if (!isBigDecimalEqual(this.withdrawn, actual.withdrawn)) {
            this.withdrawn = actual.withdrawn;
            isChanged = true;
        }

        if (!isOffsetDateTimeEqual(this.updatedAt, actual.updatedAt)) {
            this.updatedAt = actual.updatedAt;
            isChanged = true;
        }

        return isChanged;
    }
}