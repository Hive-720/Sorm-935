package com.grunvald.sorm.service.whmcsdata.filter;

import com.grunvald.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ClientGroupFilter {
    private DateTimeBaseFilter updatedAt;
    private List<ClientGroupFilter> and;
    private List<ClientGroupFilter> or;
    private ClientGroupFilter not;
}