package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.user.dto.UserSearchResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.UserResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.UserPresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = UserPresentationMapper.class)
public interface UserResultMapper {

    @Mapping(target = "result", source = "userList")
    UserResult map(UserSearchResult result);
}