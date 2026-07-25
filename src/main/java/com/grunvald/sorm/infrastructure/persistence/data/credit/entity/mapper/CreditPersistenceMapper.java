package com.grunvald.sorm.infrastructure.persistence.data.credit.entity.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit;
import com.grunvald.sorm.infrastructure.persistence.data.credit.entity.CreditDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CreditPersistenceMapper {

    Credit map(CreditDb creditDb);

    CreditDb map(Credit creditDb);
}
