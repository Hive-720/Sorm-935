package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.base.InnBaseFilter;
import com.kjsoft.sorm.graphql.generated.model.InnBaseFilterGql;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.StringTermExactFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {StringTermExactFilterPresentationMapper.class})
public interface InnBaseFilterPresentationMapper {

    InnBaseFilter map(InnBaseFilterGql filter);
}