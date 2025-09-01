package com.kjsoft.sorm.service.whmcsdata.filter.condition;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StringRegExpFullTextCondition {
    private String regexp;
    private String allOfText;
    private String anyOfText;
}