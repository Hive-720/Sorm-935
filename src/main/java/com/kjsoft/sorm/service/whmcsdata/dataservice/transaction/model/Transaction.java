package com.kjsoft.sorm.service.whmcsdata.dataservice.transaction.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

import static com.kjsoft.sorm.util.EqualsUtils.isBigDecimalEqual;


@Getter
@Setter
public class Transaction {
    private Long id;
    private Long invoiceId;
    private Long clientId;
    private String transId;
    private Long refundId;
    private String currency;
    private String gateway;
    private String description;
    private BigDecimal amountIn;
    private BigDecimal amountOut;
    private BigDecimal fees;
    private BigDecimal rate;
    private OffsetDateTime date;

    public boolean update(Transaction actual) {
        var changed = false;

        if (!Objects.equals(this.transId, actual.transId)) {
            this.transId = actual.transId;
            changed = true;
        }

        //TODO: возможно его нельзя добавить к существующей транзе, а для возврата создается новая транза. Соответственно это может быть и не нужно обновлять
        if (!Objects.equals(this.refundId, actual.refundId)) {
            this.refundId = actual.refundId;
            changed = true;
        }

        if (!Objects.equals(this.currency, actual.currency)) {
            this.currency = actual.currency;
            changed = true;
        }

        if (!Objects.equals(this.gateway, actual.gateway)) {
            this.gateway = actual.gateway;
            changed = true;
        }

        if (!Objects.equals(this.description, actual.description)) {
            this.description = actual.description;
            changed = true;
        }

        if (!isBigDecimalEqual(amountIn, actual.amountIn)) {
            this.amountIn = actual.amountIn;
            changed = true;
        }

        if (!isBigDecimalEqual(amountOut, actual.amountOut)) {
            this.amountOut = actual.amountOut;
            changed = true;
        }

        if (!isBigDecimalEqual(fees, actual.fees)) {
            this.fees = actual.fees;
            changed = true;
        }

        if (!isBigDecimalEqual(rate, actual.rate)) {
            this.rate = actual.rate;
            changed = true;
        }

        return changed;
    }
}