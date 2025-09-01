package com.kjsoft.sorm.infrastructure.persistence.data.email.entity.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.email.model.Email;
import com.kjsoft.sorm.infrastructure.persistence.data.email.entity.EmailDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface EmailPersistenceMapper {

    Email map(EmailDb email);

    EmailDb map(Email email);
}