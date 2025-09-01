package com.kjsoft.sorm.service.whmcsdata.filter.base;

import com.kjsoft.sorm.service.whmcsdata.filter.condition.StringTermExactCondition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class InnBaseFilter {
    private StringTermExactCondition inn;
    private List<InnBaseFilter> and;
    private List<InnBaseFilter> or;
    private InnBaseFilter not;
}