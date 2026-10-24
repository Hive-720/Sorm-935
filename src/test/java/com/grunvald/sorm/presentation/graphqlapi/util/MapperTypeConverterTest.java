package com.grunvald.sorm.presentation.graphqlapi.util;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DomainNameBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.EmailBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.InnBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpAddressBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.LoginBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.MccBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.MsisdnBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.OrganizationInfoBase;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType.IPV4;
import static com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType.IPV6;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MapperTypeConverterTest {

    private final MapperTypeConverter converter = new MapperTypeConverter(new MapperOffsetDateTimeConverter());

    @Test
    void stringToMsisdnBase_null_returnsNull() {
        assertNull(converter.stringToMsisdnBase(null));
    }

    @Test
    void stringToMsisdnBase_wrapsValue() {
        assertEquals(new MsisdnBase("79990001122"), converter.stringToMsisdnBase("79990001122"));
    }

    @Test
    void stringToInnBase_null_returnsNull() {
        assertNull(converter.stringToInnBase(null));
    }

    @Test
    void stringToInnBase_wrapsValue() {
        assertEquals(new InnBase("7707083893"), converter.stringToInnBase("7707083893"));
    }

    @Test
    void integerToMccBase_null_returnsNull() {
        assertNull(converter.integerToMccBase(null));
    }

    @Test
    void integerToMccBase_wrapsValue() {
        assertEquals(new MccBase(250), converter.integerToMccBase(250));
    }

    @Test
    void stringToEmailBase_null_returnsNull() {
        assertNull(converter.stringToEmailBase(null));
    }

    @Test
    void stringToEmailBase_wrapsValue() {
        assertEquals(new EmailBase("a@b.c"), converter.stringToEmailBase("a@b.c"));
    }

    @Test
    void stringListToEmailBaseList_null_returnsNull() {
        assertNull(converter.stringListToEmailBaseList(null));
    }

    @Test
    void stringListToEmailBaseList_empty_returnsNull() {
        assertNull(converter.stringListToEmailBaseList(List.of()));
    }

    @Test
    void stringListToEmailBaseList_filtersBlank() {
        List<EmailBase> result = converter.stringListToEmailBaseList(List.of("a@b.c", "", "  ", "d@e.f"));

        assertEquals(List.of(new EmailBase("a@b.c"), new EmailBase("d@e.f")), result);
    }

    @Test
    void stringListToIpAddressBase_null_returnsNull() {
        assertNull(converter.stringListToIpAddressBase(null));
    }

    @Test
    void stringListToIpAddressBase_empty_returnsNull() {
        assertNull(converter.stringListToIpAddressBase(List.of()));
    }

    @Test
    void stringListToIpAddressBase_mapsEachElement() {
        List<IpAddressBase> result = converter.stringListToIpAddressBase(List.of("192.168.1.1", "2001:db8::1"));

        assertEquals(List.of(
                new IpAddressBase("192.168.1.1", IPV4),
                new IpAddressBase("2001:db8::1", IPV6)), result);
    }

    @Test
    void stringListToIpAddressBase_doesNotFilterBlank() {
        // фактическое поведение: blank-элементы НЕ фильтруются и попадают в результат как IPV4
        List<IpAddressBase> result = converter.stringListToIpAddressBase(List.of("1.2.3.4", ""));

        assertEquals(List.of(
                new IpAddressBase("1.2.3.4", IPV4),
                new IpAddressBase("", IPV4)), result);
    }

    @Test
    void stringToIpAddressBase_null_returnsNull() {
        assertNull(converter.stringToIpAddressBase(null));
    }

    @Test
    void stringToIpAddressBase_withColon_returnsIpv6() {
        assertEquals(new IpAddressBase("2001:db8::1", IPV6), converter.stringToIpAddressBase("2001:db8::1"));
        assertEquals(new IpAddressBase("::1", IPV6), converter.stringToIpAddressBase("::1"));
    }

    @Test
    void stringToIpAddressBase_withoutColon_returnsIpv4() {
        assertEquals(new IpAddressBase("192.168.1.1", IPV4), converter.stringToIpAddressBase("192.168.1.1"));
    }

    @Test
    void stringToDomainNameBase_null_returnsNull() {
        assertNull(converter.stringToDomainNameBase(null));
    }

    @Test
    void stringToDomainNameBase_wrapsValue() {
        assertEquals(new DomainNameBase("a.ru"), converter.stringToDomainNameBase("a.ru"));
    }

    @Test
    void stringToOrganisationInfoBase_null_returnsNull() {
        assertNull(converter.stringToOrganisationInfoBase(null));
    }

    @Test
    void stringToOrganisationInfoBase_wrapsValue() {
        assertEquals(new OrganizationInfoBase("Grunvald"), converter.stringToOrganisationInfoBase("Grunvald"));
    }

    @Test
    void offsetDateTimeToDateTimeBase_null_returnsNull() {
        assertNull(converter.offsetDateTimeToDateTimeBase(null));
    }

    @Test
    void offsetDateTimeToDateTimeBase_wrapsUtcString() {
        DateTimeBase result = converter.offsetDateTimeToDateTimeBase(OffsetDateTime.parse("2026-01-02T03:04:05+03:00"));

        assertEquals(new DateTimeBase("2026-01-02T00:04:05+00:00"), result);
    }

    @Test
    void mapDomainNameBase_null_returnsNull() {
        assertNull(converter.mapDomainNameBase(null));
    }

    @Test
    void mapDomainNameBase_wrapsValue() {
        assertEquals(new DomainNameBase("b.ru"), converter.mapDomainNameBase("b.ru"));
    }

    @Test
    void stringToLoginBase_null_returnsNull() {
        assertNull(converter.stringToLoginBase(null));
    }

    @Test
    void stringToLoginBase_wrapsValue() {
        assertEquals(new LoginBase("user-1"), converter.stringToLoginBase("user-1"));
    }
}
