package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.service.whmcsdata.filter.AffiliateFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.AffiliateFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {
        ClientFilterPresentationMapper.class,
        DateTimeBaseFilterPresentationMapper.class
})
public interface AffiliateFilterPresentationMapper {

    AffiliateFilter map(AffiliateFilterInput input);
}