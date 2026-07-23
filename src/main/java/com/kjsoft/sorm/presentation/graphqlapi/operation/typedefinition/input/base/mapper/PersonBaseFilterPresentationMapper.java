package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.base.PersonBaseFilter;
import com.kjsoft.sorm.graphql.generated.model.PersonBaseFilterGql;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.StringTermExactFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.StringTermFullTextFilterPresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {
        StringTermFullTextFilterPresentationMapper.class,
        StringTermExactFilterPresentationMapper.class
})
public interface PersonBaseFilterPresentationMapper {

    @Mapping(target = "firstName", source = "name")
    PersonBaseFilter map(PersonBaseFilterGql filter);
}