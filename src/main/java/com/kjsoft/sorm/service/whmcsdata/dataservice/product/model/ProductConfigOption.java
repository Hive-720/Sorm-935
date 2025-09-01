package com.kjsoft.sorm.service.whmcsdata.dataservice.product.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;


@Getter
@Setter
public class ProductConfigOption {
    private Long id;
    private Long productConfigId;
    private String name;
    private List<Price> prices;


    public boolean update(ProductConfigOption actual) {
        var changed = false;

        if (!Objects.equals(this.name, actual.name)) {
            this.name = actual.name;
            changed = true;
        }

        if (!Price.isEqual(prices, actual.prices)) {
            this.prices = actual.prices;
            changed = true;
        }

        return changed;
    }
}