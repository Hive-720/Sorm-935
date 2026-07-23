package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.kjsoft.sorm.graphql.generated.model.ClientLoginFilterInputGql;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import com.kjsoft.sorm.service.whmcsdata.filter.ClientLoginFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {
        ClientFilterPresentationMapper.class,
        DateTimeBaseFilterPresentationMapper.class
})
public interface ClientLoginFilterPresentationMapper {

    ClientLoginFilter map(ClientLoginFilterInputGql filter);
}