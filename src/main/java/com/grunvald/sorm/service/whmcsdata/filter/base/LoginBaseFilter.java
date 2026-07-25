package com.grunvald.sorm.service.whmcsdata.filter.base;

import com.grunvald.sorm.service.whmcsdata.filter.condition.StringRegExpExactCondition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class LoginBaseFilter {
    private StringRegExpExactCondition login;
    private List<LoginBaseFilter> and;
    private List<LoginBaseFilter> or;
    private LoginBaseFilter not;
}