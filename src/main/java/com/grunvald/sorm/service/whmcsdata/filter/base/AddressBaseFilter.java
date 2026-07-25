package com.grunvald.sorm.service.whmcsdata.filter.base;

import com.grunvald.sorm.service.whmcsdata.filter.condition.StringExactCondition;
import com.grunvald.sorm.service.whmcsdata.filter.condition.StringRegExpFullTextCondition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class AddressBaseFilter {
    private StringRegExpFullTextCondition address;
    private StringExactCondition zip;
    private StringExactCondition country;
    private StringExactCondition region;
    private StringExactCondition city;
    private List<AddressBaseFilter> and;
    private List<AddressBaseFilter> or;
    private AddressBaseFilter not;
}