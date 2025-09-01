package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetOrdersResponse;
import com.kjsoft.sorm.service.whmcsprovider.dto.OrderProviderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static com.kjsoft.sorm.util.CurrencyUtil.getValidCurrency;


@Mapper(componentModel = "spring")
public interface OrderProviderMapper {

    @Mapping(source = ".", target = "currency", qualifiedByName = "buildCurrency")
    OrderProviderDto map(GetOrdersResponse.Orders.Order order);

    @Named("buildCurrency")
    default String buildCurrency(GetOrdersResponse.Orders.Order order) {
        if (order == null) {
            return null;
        }

        var currency = getValidCurrency(order.getCurrencyPrefix());
        return currency != null ? currency : getValidCurrency(order.getCurrencySuffix());
    }
}