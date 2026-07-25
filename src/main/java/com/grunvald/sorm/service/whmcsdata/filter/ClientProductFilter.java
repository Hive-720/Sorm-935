package com.grunvald.sorm.service.whmcsdata.filter;

import com.grunvald.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter;
import com.grunvald.sorm.service.whmcsdata.filter.base.DomainNameBaseFilter;
import com.grunvald.sorm.service.whmcsdata.filter.base.IpAddressBaseFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ClientProductFilter {
    private ClientFilter client;
    private OrderFilter order;
    private ProductFilter product;
    private DomainNameBaseFilter domain;
    private IpAddressBaseFilter dedicatedIpAddress;
    private IpAddressBaseFilter assignedIpAddresses;
    private DateTimeBaseFilter regDate;
    private DateTimeBaseFilter nextDueDate;
    private DateTimeBaseFilter lastUpdate;
    private List<ClientProductFilter> and;
    private List<ClientProductFilter> or;
    private ClientProductFilter not;
}