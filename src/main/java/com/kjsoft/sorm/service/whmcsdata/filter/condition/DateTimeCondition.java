package com.kjsoft.sorm.service.whmcsdata.filter.condition;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;


@Getter
@Setter
public class DateTimeCondition {
    private OffsetDateTime eq;
    private List<OffsetDateTime> in;
    private OffsetDateTime le;
    private OffsetDateTime lt;
    private OffsetDateTime ge;
    private OffsetDateTime gt;
    private DateTimeRangeCondition between;
}