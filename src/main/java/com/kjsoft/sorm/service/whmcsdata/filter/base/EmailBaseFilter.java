package com.kjsoft.sorm.service.whmcsdata.filter.base;

import com.kjsoft.sorm.service.whmcsdata.filter.condition.StringRegExpExactCondition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class EmailBaseFilter {
    private StringRegExpExactCondition email;
    private List<EmailBaseFilter> and;
    private List<EmailBaseFilter> or;
    private EmailBaseFilter not;
}