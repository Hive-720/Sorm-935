package com.kjsoft.sorm.infrastructure.persistence.data.credit.entity.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.credit.model.Credit;
import com.kjsoft.sorm.infrastructure.persistence.data.credit.entity.CreditDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CreditPersistenceMapper {

    Credit map(CreditDb creditDb);

    CreditDb map(Credit creditDb);
}
