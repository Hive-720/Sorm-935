package com.grunvald.sorm.service.whmcsdata.dataservice.client.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

import static com.grunvald.sorm.util.EqualsUtils.isBigDecimalEqual;


@Getter
@Setter
public class ClientGroup {
    private Long id;
    private String name;
    private BigDecimal discountPercent;
    private OffsetDateTime updatedAt;
    private boolean deleted;

    public ClientGroup(String name, BigDecimal discountPercent) {
        this.name = name;
        this.discountPercent = discountPercent;
    }

    public boolean update(ClientGroup actual) {
        var changed = false;

        if (!Objects.equals(name, actual.name)) {
            this.name = actual.name;
            changed = true;
        }

        if (!isBigDecimalEqual(discountPercent, actual.discountPercent)) {
            this.discountPercent = actual.discountPercent;
            changed = true;
        }

        if (!Objects.equals(deleted, actual.deleted)) {
            this.deleted = actual.deleted;
            changed = true;
        }

        return changed;
    }
}