package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.ClientGroupFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientGroupFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = DateTimeBaseFilterPresentationMapper.class)
public interface ClientGroupFilterPresentationMapper {

    ClientGroupFilter map(ClientGroupFilterInput filter);
}