package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base;

import com.kjsoft.sorm.graphql.generated.model.IpTypeGql;

public record IpAddressBase(String ip, IpTypeGql type) {
}