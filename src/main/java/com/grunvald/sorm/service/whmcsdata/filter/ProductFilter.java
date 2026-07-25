package com.grunvald.sorm.service.whmcsdata.filter;

import com.grunvald.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ProductFilter {
    private DateTimeBaseFilter updatedAt;
    private List<ProductFilter> and;
    private List<ProductFilter> or;
    private ProductFilter not;
}