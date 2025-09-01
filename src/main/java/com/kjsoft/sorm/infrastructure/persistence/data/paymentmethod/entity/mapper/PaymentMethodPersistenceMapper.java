package com.kjsoft.sorm.infrastructure.persistence.data.paymentmethod.entity.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;
import com.kjsoft.sorm.infrastructure.persistence.data.paymentmethod.entity.PaymentMethodDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PaymentMethodPersistenceMapper {

    PaymentMethod map(PaymentMethodDb paymentMethodDb);

    PaymentMethodDb map(PaymentMethod paymentMethod);
}