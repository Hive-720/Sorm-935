package com.grunvald.sorm.service.whmcsdata.filter.base;

import com.grunvald.sorm.service.whmcsdata.filter.condition.DateTimeCondition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class DateTimeBaseFilter {
    private DateTimeCondition utc;
    private List<DateTimeBaseFilter> and;
    private List<DateTimeBaseFilter> or;
    private DateTimeBaseFilter not;
}