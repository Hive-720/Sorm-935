package com.kjsoft.sorm.service.whmcsdata.filter;

import com.kjsoft.sorm.service.whmcsdata.filter.base.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ContactFilter {
    private ClientFilter client;
    private PersonBaseFilter name;
    private OrganizationInfoBaseFilter organizationInfo;
    private EmailBaseFilter email;
    private AddressBaseFilter address;
    private InnBaseFilter inn;
    private MsisdnBaseFilter msisdn;
    private DateTimeBaseFilter updatedAt;
    private DateTimeBaseFilter createdAt;
    private List<ContactFilter> and;
    private List<ContactFilter> or;
    private ContactFilter not;
}