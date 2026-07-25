package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.user.dto.UserSearchResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.UserResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.UserPresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = UserPresentationMapper.class)
public interface UserResultMapper {

    @Mapping(target = "result", source = "userList")
    UserResult map(UserSearchResult result);
}