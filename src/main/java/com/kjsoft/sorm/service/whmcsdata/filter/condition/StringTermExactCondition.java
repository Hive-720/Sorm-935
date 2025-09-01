package com.kjsoft.sorm.service.whmcsdata.filter.condition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class StringTermExactCondition {
    List<String> allOfTerms;
    List<String> anyOfTerms;
    String eq;
    List<String> in;
}