package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.paymentmethod.mapper;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.paymentmethod.PaymentMethodPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PaymentMethodFilterPersistenceMapper {

    PaymentMethodPersistenceFilter map(com.kjsoft.sorm.service.whmcsdata.filter.PaymentMethodFilter filter);
}