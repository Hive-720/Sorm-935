package com.grunvald.sorm.service.whmcsdata.filter.base;

import com.grunvald.sorm.service.whmcsdata.filter.condition.StringRegExpExactCondition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class MsisdnBaseFilter {
    private StringRegExpExactCondition msisdn;
    private List<MsisdnBaseFilter> and;
    private List<MsisdnBaseFilter> or;
    private MsisdnBaseFilter not;
}