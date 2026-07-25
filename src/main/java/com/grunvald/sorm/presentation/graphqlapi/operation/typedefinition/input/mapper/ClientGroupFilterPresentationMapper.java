package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.service.whmcsdata.filter.ClientGroupFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientGroupFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = DateTimeBaseFilterPresentationMapper.class)
public interface ClientGroupFilterPresentationMapper {

    ClientGroupFilter map(ClientGroupFilterInput filter);
}