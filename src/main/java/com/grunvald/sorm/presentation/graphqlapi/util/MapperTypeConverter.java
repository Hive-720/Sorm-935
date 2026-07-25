package com.grunvald.sorm.presentation.graphqlapi.util;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.*;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType.IPV4;
import static com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType.IPV6;


//TODO подумать и раскидать по местам если что
@Component
@AllArgsConstructor
public class MapperTypeConverter {
    private final MapperOffsetDateTimeConverter mapperOffsetDateTimeConverter;


    @Named("stringToMsisdnBase")
    public MsisdnBase stringToMsisdnBase(String msisdn) {
        if (msisdn == null) {
            return null;
        }

        return new MsisdnBase(msisdn);
    }

    @Named("stringToInnBase")
    public InnBase stringToInnBase(String taxId) {
        if (taxId == null) {
            return null;
        }

        return new InnBase(taxId);
    }

    @Named("integerToMccBase")
    public MccBase integerToMccBase(Integer mcc) {
        if (mcc == null) {
            return null;
        }

        return new MccBase(mcc);
    }

    @Named("stringListToEmailBaseList")
    public List<EmailBase> stringListToEmailBaseList(List<String> input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        return input.stream()
                .filter(Strings::isNotBlank)
                .map(this::stringToEmailBase)
                .collect(Collectors.toList());
    }

    @Named("stringToEmailBase")
    public EmailBase stringToEmailBase(String email) {
        if (email == null) {
            return null;
        }

        return new EmailBase(email);
    }

    @Named("stringListToIpAddressBase")
    public List<IpAddressBase> stringListToIpAddressBase(List<String> ipAddressList) {
        if (ipAddressList == null || ipAddressList.isEmpty()) {
            return null;
        }

        return ipAddressList.stream()
                .map(this::stringToIpAddressBase)
                .toList();
    }

    @Named("stringToIpAddressBase")
    public IpAddressBase stringToIpAddressBase(String ipAddress) {
        if (ipAddress == null) {
            return null;
        }

        var ipType = ipAddress.contains(":") ? IPV6 : IPV4;
        return new IpAddressBase(ipAddress, ipType);
    }

    @Named("stringToDomainNameBase")
    public DomainNameBase stringToDomainNameBase(String domain) {
        if (domain == null) {
            return null;
        }

        return new DomainNameBase(domain);
    }

    @Named("stringToOrganisationInfoBase")
    public OrganizationInfoBase stringToOrganisationInfoBase(String companyName) {
        if (companyName == null) {
            return null;
        }

        return new OrganizationInfoBase(companyName);
    }

    @Named("offsetDateTimeToDateTimeBase")
    public DateTimeBase offsetDateTimeToDateTimeBase(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }

        return new DateTimeBase(mapperOffsetDateTimeConverter.offsetDateTimeToUtcString(offsetDateTime));
    }

    @Named("mapDomainNameBase")
    public DomainNameBase mapDomainNameBase(String domain) {
        if (domain == null) {
            return null;
        }

        return new DomainNameBase(domain);
    }

    @Named("stringToLoginBase")
    public LoginBase stringToLoginBase(String username) {
        if (username == null) {
            return null;
        }

        return new LoginBase(username);
    }
}