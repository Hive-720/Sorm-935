package com.grunvald.sorm.service.whmcsdata.filter;

import com.grunvald.sorm.service.whmcsdata.filter.base.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ClientFilter {
    private PersonBaseFilter name;
    private UserFilter userOwner;
    private ClientGroupFilter clientGroup;
    private OrganizationInfoBaseFilter organizationInfo;
    private InnBaseFilter inn;
    private EmailBaseFilter email;
    private MsisdnBaseFilter msisdn;
    private MccBaseFilter mcc;
    private AddressBaseFilter address;
    private DateTimeBaseFilter createdAt;
    private List<ClientFilter> and;
    private List<ClientFilter> or;
    private ClientFilter not;
}