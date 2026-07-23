package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.UserFilter;
import com.kjsoft.sorm.graphql.generated.model.UserFilterInputGql;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.PersonBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = PersonBaseFilterPresentationMapper.class)
public interface UserFilterPresentationMapper {

    UserFilter map(UserFilterInputGql input);
}