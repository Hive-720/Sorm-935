package com.kjsoft.sorm.service.whmcsprovider.dto.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.email.model.Email;
import com.kjsoft.sorm.service.whmcsprovider.dto.EmailProviderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface EmailMapper {

    @Mapping(target = "messageBody", expression = "java(messageBody)")
    @Mapping(target = "clientId", expression = "java(clientId)")
    Email map(EmailProviderDto dto, String messageBody, Long clientId);
}