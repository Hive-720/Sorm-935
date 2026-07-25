package com.grunvald.sorm.service.whmcsdata.filter.condition;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class DateTimeRangeCondition {
    private OffsetDateTime min;
    private OffsetDateTime max;
}