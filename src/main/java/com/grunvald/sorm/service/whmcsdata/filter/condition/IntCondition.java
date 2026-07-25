package com.grunvald.sorm.service.whmcsdata.filter.condition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class IntCondition {
    private Integer eq;
    private List<Integer> in;
    private Integer le;
    private Integer lt;
    private Integer ge;
    private Integer gt;
    private IntRangeCondition between;
}