package com.grunvald.sorm.service.whmcsdata.filter.condition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class StringTermFullTextCondition {
    private List<String> allOfTerms;
    private List<String> anyOfTerms;
    private String allOfText;
    private String anyOfText;
}