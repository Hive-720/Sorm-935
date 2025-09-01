package com.kjsoft.sorm.service.whmcsdata.datasyncservice.product;

import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.Product;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfig;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfigOption;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.ProductConfigRelation;
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