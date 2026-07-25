package com.grunvald.sorm.service.whmcsdata.filter.condition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class StringExactCondition {
   private String eq;
   private List<String> in;

}