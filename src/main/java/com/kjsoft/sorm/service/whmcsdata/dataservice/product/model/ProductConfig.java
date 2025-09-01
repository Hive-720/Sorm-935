package com.kjsoft.sorm.service.whmcsdata.dataservice.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
@AllArgsConstructor
public class ProductConfig {
    private Long id;
    private String name;
    private String type;

    public boolean update(ProductConfig actual) {
        var changed = false;

        if (!Objects.equals(this.name, actual.name)) {
            this.name = actual.name;
            changed = true;
        }

        if (!Objects.equals(this.type, actual.type)) {
            this.type = actual.type;
            changed = true;
        }

        return changed;
    }
}