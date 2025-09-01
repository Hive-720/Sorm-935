package com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;


@Getter
@Setter
public class PaymentMethod {
    private Long id;
    private String module;
    private String displayName;
    private OffsetDateTime updatedAt;
    private boolean deleted;

    public boolean update(PaymentMethod actual) {
        var changed = false;

        if (!Objects.equals(this.displayName, actual.displayName)) {
            this.displayName = actual.displayName;
            changed = true;
        }

        if (!Objects.equals(this.deleted, actual.deleted)) {
            this.deleted = actual.deleted;
            changed = true;
        }

        return changed;
    }
}