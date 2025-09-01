package com.kjsoft.sorm.service.whmcsdata.filter.base;

import com.kjsoft.sorm.service.whmcsdata.filter.condition.StringRegExpExactCondition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class OrganizationInfoBaseFilter {
    private StringRegExpExactCondition nameFull;
    private List<OrganizationInfoBaseFilter> and;
    private List<OrganizationInfoBaseFilter> or;
    private OrganizationInfoBaseFilter not;
}