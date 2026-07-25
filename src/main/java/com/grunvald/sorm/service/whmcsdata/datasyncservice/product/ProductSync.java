package com.grunvald.sorm.service.whmcsdata.datasyncservice.product;

import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Product;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfig;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption;
import com.grunvald.sorm.service.whmcsdata.dataservice.product.model.ProductConfigRelation;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class ProductSync {
    private List<Product> products;
    private List<ProductConfig> productConfigs;
    private List<ProductConfigRelation> productConfigRelations;
    private List<ProductConfigOption> productConfigOptions;
}