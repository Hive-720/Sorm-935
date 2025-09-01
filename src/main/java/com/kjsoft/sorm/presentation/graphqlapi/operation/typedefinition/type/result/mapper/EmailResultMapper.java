package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.email.dto.EmailSearchResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.EmailPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.EmailResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = EmailPresentationMapper.class)
public interface EmailResultMapper {

    @Mapping(target = "result", source = "emailList")
    EmailResult map(EmailSearchResult result);
}