package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Price;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface PricePresentationMapper {

    @Mapping(target = "mSetupFee", expression = "java(price.getMSetupFee())")
    @Mapping(target = "qSetupFee", expression = "java(price.getQSetupFee())")
    @Mapping(target = "sSetupFee", expression = "java(price.getSSetupFee())")
    @Mapping(target = "aSetupFee", expression = "java(price.getASetupFee())")
    @Mapping(target = "bSetupFee", expression = "java(price.getBSetupFee())")
    @Mapping(target = "tSetupFee", expression = "java(price.getTSetupFee())")
    Price map(com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.Price price);
}