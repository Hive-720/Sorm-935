package com.grunvald.sorm.service.whmcsdata.filter.base;

import com.grunvald.sorm.service.whmcsdata.filter.condition.StringTermExactCondition;
import com.grunvald.sorm.service.whmcsdata.filter.condition.StringTermFullTextCondition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class PersonBaseFilter {
    private StringTermFullTextCondition fullName;
    private StringTermExactCondition firstName;
    private StringTermExactCondition lastName;
    private List<PersonBaseFilter> and;
    private List<PersonBaseFilter> or;
    private PersonBaseFilter not;
}