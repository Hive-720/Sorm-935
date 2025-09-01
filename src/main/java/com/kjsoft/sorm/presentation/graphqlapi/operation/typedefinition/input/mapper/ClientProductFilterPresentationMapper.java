package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.ClientProductFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientProductFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring",
        uses = {
                ClientFilterPresentationMapper.class,
                OrderFilterPresentationMapper.class,
                ProductFilterPresentationMapper.class,
                DateTimeBaseFilterPresentationMapper.class
        })
public interface ClientProductFilterPresentationMapper {

    ClientProductFilter map(ClientProductFilterInput input);
}