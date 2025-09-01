package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.contact.dto.ContactSearchResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ContactResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ContactPresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = ContactPresentationMapper.class)
public interface ContactResultMapper {

    @Mapping(target = "result", source = "contactList")
    ContactResult map(ContactSearchResult result);
}