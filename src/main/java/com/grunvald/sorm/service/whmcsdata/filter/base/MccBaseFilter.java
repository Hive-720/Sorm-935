package com.grunvald.sorm.service.whmcsdata.filter.base;

import com.grunvald.sorm.service.whmcsdata.filter.condition.IntCondition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class MccBaseFilter {
    private IntCondition mcc;
    private List<MccBaseFilter> and;
    private List<MccBaseFilter> or;
    private MccBaseFilter not;
}