package com.kjsoft.sorm.presentation.graphqlapi.operation.queryresolver;

import com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.PaymentMethodService;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.PaymentMethodFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.PaymentMethodFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.PaymentMethodResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.PaymentMethodResultMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;


@Controller
@RequiredArgsConstructor
public class PaymentMethodResolver {
    private final PaymentMethodService paymentMethodService;
    private final PaymentMethodFilterPresentationMapper paymentMethodFilterMapper;
    private final PaymentMethodResultMapper resultMapper;


    @SchemaMapping(typeName = "QueryMessage")
    public Mono<PaymentMethodResult> paymentMethods(@Argument PaymentMethodFilterInput filter, @Argument Integer offset,
                                            @Argument Integer limit, @Argument String cursor) {
        var searchFilter = paymentMethodFilterMapper.map(filter);
        return paymentMethodService.search(searchFilter, cursor, offset, limit)
                .map(resultMapper::map);
    }
}