package com.grunvald.sorm.service.whmcsdata.filter.base;

import com.grunvald.sorm.service.whmcsdata.filter.condition.StringRegExpExactCondition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class DomainNameBaseFilter {
    private StringRegExpExactCondition domainName;
    private List<DomainNameBaseFilter> and;
    private List<DomainNameBaseFilter> or;
    private DomainNameBaseFilter not;
}