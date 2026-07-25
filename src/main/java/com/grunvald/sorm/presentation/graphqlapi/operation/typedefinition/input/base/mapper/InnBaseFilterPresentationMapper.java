package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper;

import com.grunvald.sorm.service.whmcsdata.filter.base.InnBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.StringTermExactFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {StringTermExactFilterPresentationMapper.class})
public interface InnBaseFilterPresentationMapper {

    InnBaseFilter map(com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.InnBaseFilter filter);
}