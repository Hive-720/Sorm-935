package com.grunvald.sorm.service.whmcsdata.filter.condition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class StringRegExpExactCondition {
    private String regexp;
    private String eq;
    private List<String> in;
}