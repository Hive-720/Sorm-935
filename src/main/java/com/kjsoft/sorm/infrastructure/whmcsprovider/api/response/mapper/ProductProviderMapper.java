package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper;


import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetProductsResponse;
import com.kjsoft.sorm.service.whmcsprovider.dto.ProductProviderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface ProductProviderMapper {

    @Mapping(source = "pricing", target = "prices", qualifiedByName = "mapPricingToPrices")
    ProductProviderDto map(GetProductsResponse.Products.Product product);

    @Mapping(source = "pricing", target = "prices", qualifiedByName = "mapPricingToPrices")
    ProductProviderDto.Config.Option optionToOption(GetProductsResponse.Products.Product.ProductConfig.Config.Options.Option option);

    @Named("mapPricingToPrices")
    default List<ProductProviderDto.Price> mapPricingToPrices(Map<String, GetProductsResponse.Pricing> pricingMap) {
        if (pricingMap == null) {
            return null;
        }

        return pricingMap.entrySet().stream()
                .map(entry -> {
                    var pricing = entry.getValue();

                    var price = new ProductProviderDto.Price();
                    price.setCurrency(entry.getKey());
                    price.setMSetupFee(pricing.getMSetupFee());
                    price.setQSetupFee(pricing.getQSetupFee());
                    price.setSSetupFee(pricing.getSSetupFee());
                    price.setASetupFee(pricing.getASetupFee());
                    price.setBSetupFee(pricing.getBSetupFee());
                    price.setTSetupFee(pricing.getTSetupFee());
                    price.setMonthly(pricing.getMonthly());
                    price.setQuarterly(pricing.getQuarterly());
                    price.setSemiannually(pricing.getSemiannually());
                    price.setAnnually(pricing.getAnnually());
                    price.setBiennially(pricing.getBiennially());
                    price.setTriennially(pricing.getTriennially());

                    return price;
                })
                .toList();
    }
}