package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.service.whmcsdata.filter.UserFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.UserFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.PersonBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = PersonBaseFilterPresentationMapper.class)
public interface UserFilterPresentationMapper {

    UserFilter map(UserFilterInput input);
}