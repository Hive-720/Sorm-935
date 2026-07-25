package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.paymentmethod.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.paymentmethod.PaymentMethodPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PaymentMethodFilterPersistenceMapper {

    PaymentMethodPersistenceFilter map(com.grunvald.sorm.service.whmcsdata.filter.PaymentMethodFilter filter);
}