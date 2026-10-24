package com.grunvald.sorm.presentation.graphqlapi.support;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientGroupFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.InvoiceFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.OrderFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.OrderItemFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ProductFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.UserFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.AddressBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DomainNameBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.EmailBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.InnBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.IpAddressBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.LoginBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.MccBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.MsisdnBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.OrganizationInfoBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.PersonBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.DateTimeRange;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.DateTimeStringFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.IntFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.IntRange;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpFullTextFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringTermFullTextFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;

import java.util.List;


public final class SampleFilters {

    public static final String D01 = "2026-01-01T00:00:00Z";
    public static final String D02 = "2026-01-02T00:00:00Z";
    public static final String D03 = "2026-01-03T00:00:00Z";
    public static final String D04 = "2026-01-04T00:00:00Z";
    public static final String D05 = "2026-01-05T00:00:00Z";
    public static final String D06 = "2026-01-06T00:00:00Z";
    public static final String D07 = "2026-01-07T00:00:00Z";
    public static final String D08 = "2026-01-08T00:00:00Z";
    public static final String D09 = "2026-01-09T00:00:00Z";
    public static final String D10 = "2026-01-10T00:00:00Z";
    public static final String D11 = "2026-01-11T00:00:00Z";
    public static final String D12 = "2026-01-12T00:00:00Z";
    public static final String D13 = "2026-01-13T00:00:00Z";
    public static final String D14 = "2026-01-14T00:00:00Z";
    public static final String D15 = "2026-01-15T00:00:00Z";

    private SampleFilters() {
    }

    /** eq = parameter, in = [D03, D04], le = D05, lt = D06, ge = D07, gt = D08, between = (D09, D10). */
    public static DateTimeStringFilter dateTimeString(String eq) {
        return new DateTimeStringFilter(eq, List.of(D03, D04), D05, D06, D07, D08, new DateTimeRange(D09, D10));
    }

    public static DateTimeBaseFilter dateTime(String eq) {
        return new DateTimeBaseFilter(dateTimeString(eq), null, null, null);
    }

    public static PersonBaseFilter person() {
        PersonBaseFilter nested = new PersonBaseFilter(
                null,
                new StringTermExactFilter("nested-exact", null, "nested-eq", List.of("nested-in")),
                null, null, null, null);
        return new PersonBaseFilter(
                new StringTermFullTextFilter("alpha,beta", "gamma", "all-text", "any-text"),
                new StringTermExactFilter("exact-a,exact-b", "exact-any", "exact-eq", List.of("in-1", "in-2")),
                new StringTermExactFilter("last-a", null, "last-eq", null),
                List.of(nested), List.of(nested), nested);
    }

    public static EmailBaseFilter emailBase() {
        EmailBaseFilter nested = new EmailBaseFilter(
                new StringRegExpExactFilter("nested-re", "nested-eq", List.of("nested-in")), null, null, null);
        return new EmailBaseFilter(
                new StringRegExpExactFilter(".*@mail\\.com", "eq@mail.com", List.of("in-1@mail.com", "in-2@mail.com")),
                List.of(nested), List.of(nested), nested);
    }

    public static IpAddressBaseFilter ipAddress() {
        IpAddressBaseFilter nested = new IpAddressBaseFilter(
                new StringRegExpExactFilter("fe80.*", null, null), IpType.IPV6, null, null, null);
        return new IpAddressBaseFilter(
                new StringRegExpExactFilter("10\\.0\\..*", "10.0.0.1", List.of("10.0.0.2", "10.0.0.3")),
                IpType.IPV4,
                List.of(nested), List.of(nested), nested);
    }

    public static LoginBaseFilter loginBase() {
        LoginBaseFilter nested = new LoginBaseFilter(
                new StringRegExpExactFilter("nested-login", null, null), null, null, null);
        return new LoginBaseFilter(
                new StringRegExpExactFilter("user.*", "user-1", List.of("user-2")),
                List.of(nested), List.of(nested), nested);
    }

    public static DomainNameBaseFilter domainName() {
        DomainNameBaseFilter nested = new DomainNameBaseFilter(
                new StringRegExpExactFilter("nested\\.com", null, null), null, null, null);
        return new DomainNameBaseFilter(
                new StringRegExpExactFilter(".*\\.ru", "a.ru", List.of("b.ru")),
                List.of(nested), List.of(nested), nested);
    }

    public static OrganizationInfoBaseFilter organizationInfo() {
        OrganizationInfoBaseFilter nested = new OrganizationInfoBaseFilter(
                new StringRegExpExactFilter("Nested.*", null, null), null, null, null);
        return new OrganizationInfoBaseFilter(
                new StringRegExpExactFilter("Grunvald.*", "Grunvald", List.of("Other")),
                List.of(nested), List.of(nested), nested);
    }

    public static MsisdnBaseFilter msisdn() {
        MsisdnBaseFilter nested = new MsisdnBaseFilter(
                new StringRegExpExactFilter("7999.*", null, null), null, null, null);
        return new MsisdnBaseFilter(
                new StringRegExpExactFilter("79.*", "79990001122", List.of("79990001133")),
                List.of(nested), List.of(nested), nested);
    }

    public static MccBaseFilter mcc() {
        MccBaseFilter nested = new MccBaseFilter(new IntFilter(999, null, null, null, null, null, null), null, null, null);
        return new MccBaseFilter(
                new IntFilter(250, List.of(251, 252), 253, 254, 255, 256, new IntRange(1, 999)),
                List.of(nested), List.of(nested), nested);
    }

    public static InnBaseFilter inn() {
        InnBaseFilter nested = new InnBaseFilter(
                new StringTermExactFilter("nested-inn", null, "nested-eq", null), null, null, null);
        return new InnBaseFilter(
                new StringTermExactFilter("inn-a,inn-b", "inn-any", "inn-eq", List.of("inn-in-1")),
                List.of(nested), List.of(nested), nested);
    }

    public static AddressBaseFilter address() {
        AddressBaseFilter nested = new AddressBaseFilter(
                new StringRegExpFullTextFilter("nested-re", "nested-all", "nested-any"),
                null, null, null, null, null, null, null);
        return new AddressBaseFilter(
                new StringRegExpFullTextFilter("re.*", "all-text", "any-text"),
                new StringExactFilter("zip-1", List.of("zip-2", "zip-3")),
                new StringExactFilter("RU", null),
                new StringExactFilter("MSK", null),
                new StringExactFilter("Moscow", null),
                List.of(nested), List.of(nested), nested);
    }

    public static OrderItemFilter orderItem() {
        OrderItemFilter nested = new OrderItemFilter(
                new DomainNameBaseFilter(new StringRegExpExactFilter("nested\\.com", null, null), null, null, null),
                null, null, null);
        return new OrderItemFilter(domainName(), List.of(nested), List.of(nested), nested);
    }

    public static UserFilterInput user() {
        UserFilterInput nested = new UserFilterInput(null, null, null, null, null);
        return new UserFilterInput(person(), emailBase(), List.of(nested), List.of(nested), nested);
    }

    public static ClientGroupFilterInput clientGroup() {
        ClientGroupFilterInput nested = new ClientGroupFilterInput(null, null, null, null);
        return new ClientGroupFilterInput(dateTime(D02), List.of(nested), List.of(nested), nested);
    }

    public static ClientFilterInput client() {
        ClientFilterInput nested = new ClientFilterInput(
                null, null, null, null, null, null, null, null, null, null, null, null, null);
        return new ClientFilterInput(
                person(), user(), clientGroup(), organizationInfo(), inn(), emailBase(), msisdn(), mcc(),
                address(), dateTime(D01),
                List.of(nested), List.of(nested), nested);
    }

    public static InvoiceFilterInput invoice() {
        InvoiceFilterInput nested = new InvoiceFilterInput(
                null, null, null, null, null, null, null, null, null, null, null);
        return new InvoiceFilterInput(
                client(),
                dateTime(D01), dateTime(D02), dateTime(D03), dateTime(D04),
                dateTime(D05), dateTime(D06), dateTime(D07),
                List.of(nested), List.of(nested), nested);
    }

    public static OrderFilterInput order() {
        OrderFilterInput nested = new OrderFilterInput(null, null, null, null, null, null, null, null);
        return new OrderFilterInput(
                client(), invoice(), ipAddress(), orderItem(), dateTime(D08),
                List.of(nested), List.of(nested), nested);
    }

    public static ProductFilterInput product() {
        ProductFilterInput nested = new ProductFilterInput(null, null, null, null);
        return new ProductFilterInput(dateTime(D09), List.of(nested), List.of(nested), nested);
    }
}
