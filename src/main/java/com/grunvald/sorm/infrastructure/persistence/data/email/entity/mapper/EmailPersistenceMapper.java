package com.grunvald.sorm.infrastructure.persistence.data.email.entity.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email;
import com.grunvald.sorm.infrastructure.persistence.data.email.entity.EmailDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface EmailPersistenceMapper {

    Email map(EmailDb email);

    EmailDb map(Email email);
}