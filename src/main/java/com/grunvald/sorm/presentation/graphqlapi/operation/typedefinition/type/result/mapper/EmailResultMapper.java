package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.email.dto.EmailSearchResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.EmailPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.EmailResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = EmailPresentationMapper.class)
public interface EmailResultMapper {

    @Mapping(target = "result", source = "emailList")
    EmailResult map(EmailSearchResult result);
}