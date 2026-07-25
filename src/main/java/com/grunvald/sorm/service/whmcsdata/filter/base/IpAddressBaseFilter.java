package com.grunvald.sorm.service.whmcsdata.filter.base;

import com.grunvald.sorm.service.whmcsdata.filter.condition.StringRegExpExactCondition;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class IpAddressBaseFilter {
    private StringRegExpExactCondition ip;
    private IpType type;
    private List<IpAddressBaseFilter> and;
    private List<IpAddressBaseFilter> or;
    private IpAddressBaseFilter not;
}