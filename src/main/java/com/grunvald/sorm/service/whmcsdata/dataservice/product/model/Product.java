package com.grunvald.sorm.service.whmcsdata.dataservice.product.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static com.grunvald.sorm.util.EqualsUtils.isOffsetDateTimeEqual;


@Getter
@Setter
public class Product {
    private Long id;
    private String type;
    private String payType;
    private String name;
    private String description;
    private List<Price> prices;
    private OffsetDateTime updatedAt;

    private boolean deleted;

    public boolean update(Product actual) {
        var changed = false;

        if (!Objects.equals(this.type, actual.type)) {
            this.type = actual.type;
            changed = true;
        }

        if (!Objects.equals(this.payType, actual.payType)) {
            this.payType = actual.payType;
            changed = true;
        }

        if (!Objects.equals(this.name, actual.name)) {
            this.name = actual.name;
            changed = true;
        }

        if (!Objects.equals(this.description, actual.description)) {
            this.description = actual.description;
            changed = true;
        }

        if (Price.isEqual(this.prices, actual.prices)) {
            this.prices = actual.prices;
            changed = true;
        }

        if (!isOffsetDateTimeEqual(updatedAt, actual.updatedAt)) {
            this.updatedAt = actual.updatedAt;
            changed = true;
        }

        return changed;
    }
}