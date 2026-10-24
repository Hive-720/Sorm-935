package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;
import com.grunvald.sorm.service.whmcsdata.filter.ActivityLogFilter;
import com.grunvald.sorm.service.whmcsdata.filter.AffiliateFilter;
import com.grunvald.sorm.service.whmcsdata.filter.ClientFilter;
import com.grunvald.sorm.service.whmcsdata.filter.ClientGroupFilter;
import com.grunvald.sorm.service.whmcsdata.filter.ContactFilter;
import com.grunvald.sorm.service.whmcsdata.filter.CreditFilter;
import com.grunvald.sorm.service.whmcsdata.filter.EmailFilter;
import com.grunvald.sorm.service.whmcsdata.filter.InvoiceFilter;
import com.grunvald.sorm.service.whmcsdata.filter.OrderFilter;
import com.grunvald.sorm.service.whmcsdata.filter.OrderItemFilter;
import com.grunvald.sorm.service.whmcsdata.filter.PaymentMethodFilter;
import com.grunvald.sorm.service.whmcsdata.filter.ProductFilter;
import com.grunvald.sorm.service.whmcsdata.filter.TransactionFilter;
import com.grunvald.sorm.service.whmcsdata.filter.UserFilter;
import com.grunvald.sorm.service.whmcsdata.filter.base.AddressBaseFilter;
import com.grunvald.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter;
import com.grunvald.sorm.service.whmcsdata.filter.base.DomainNameBaseFilter;
import com.grunvald.sorm.service.whmcsdata.filter.base.EmailBaseFilter;
import com.grunvald.sorm.service.whmcsdata.filter.base.InnBaseFilter;
import com.grunvald.sorm.service.whmcsdata.filter.base.IpAddressBaseFilter;
import com.grunvald.sorm.service.whmcsdata.filter.base.LoginBaseFilter;
import com.grunvald.sorm.service.whmcsdata.filter.base.MccBaseFilter;
import com.grunvald.sorm.service.whmcsdata.filter.base.MsisdnBaseFilter;
import com.grunvald.sorm.service.whmcsdata.filter.base.OrganizationInfoBaseFilter;
import com.grunvald.sorm.service.whmcsdata.filter.base.PersonBaseFilter;
import com.grunvald.sorm.service.whmcsdata.filter.condition.DateTimeCondition;
import com.grunvald.sorm.service.whmcsdata.filter.condition.DateTimeRangeCondition;
import com.grunvald.sorm.service.whmcsdata.filter.condition.IntCondition;
import com.grunvald.sorm.service.whmcsdata.filter.condition.IntRangeCondition;
import com.grunvald.sorm.service.whmcsdata.filter.condition.StringExactCondition;
import com.grunvald.sorm.service.whmcsdata.filter.condition.StringRegExpExactCondition;
import com.grunvald.sorm.service.whmcsdata.filter.condition.StringRegExpFullTextCondition;
import com.grunvald.sorm.service.whmcsdata.filter.condition.StringTermExactCondition;
import com.grunvald.sorm.service.whmcsdata.filter.condition.StringTermFullTextCondition;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public final class PersistenceFilterTestSupport {

    public static final OffsetDateTime D01 = OffsetDateTime.parse("2026-01-01T00:00:00Z");
    public static final OffsetDateTime D02 = OffsetDateTime.parse("2026-01-02T00:00:00Z");
    public static final OffsetDateTime D03 = OffsetDateTime.parse("2026-01-03T00:00:00Z");
    public static final OffsetDateTime D04 = OffsetDateTime.parse("2026-01-04T00:00:00Z");
    public static final OffsetDateTime D05 = OffsetDateTime.parse("2026-01-05T00:00:00Z");
    public static final OffsetDateTime D06 = OffsetDateTime.parse("2026-01-06T00:00:00Z");
    public static final OffsetDateTime D07 = OffsetDateTime.parse("2026-01-07T00:00:00Z");
    public static final OffsetDateTime D08 = OffsetDateTime.parse("2026-01-08T00:00:00Z");
    public static final OffsetDateTime D09 = OffsetDateTime.parse("2026-01-09T00:00:00Z");

    private PersistenceFilterTestSupport() {
    }

    public static Object read(Object target, String fieldName) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Cannot read field '" + fieldName + "' of " + target.getClass().getName(), e);
        }
    }

    private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    // ===== conditions =====

    private static DateTimeCondition dateTimeCondition(OffsetDateTime eq) {
        DateTimeCondition condition = new DateTimeCondition();
        condition.setEq(eq);
        condition.setIn(List.of(eq.plusDays(1), eq.plusDays(2)));
        condition.setLe(eq.plusDays(3));
        condition.setLt(eq.plusDays(4));
        condition.setGe(eq.plusDays(5));
        condition.setGt(eq.plusDays(6));
        DateTimeRangeCondition between = new DateTimeRangeCondition();
        between.setMin(eq.plusDays(7));
        between.setMax(eq.plusDays(8));
        condition.setBetween(between);
        return condition;
    }

    // ===== base filters =====

    /** utc = condition(eq), nested and/or/not = condition(eq + 30 days). */
    public static DateTimeBaseFilter dateTime(OffsetDateTime eq) {
        DateTimeBaseFilter nested = new DateTimeBaseFilter();
        nested.setUtc(dateTimeCondition(eq.plusDays(30)));
        DateTimeBaseFilter filter = new DateTimeBaseFilter();
        filter.setUtc(dateTimeCondition(eq));
        filter.setAnd(List.of(nested));
        filter.setOr(List.of(nested));
        filter.setNot(nested);
        return filter;
    }

    public static PersonBaseFilter person() {
        PersonBaseFilter nested = new PersonBaseFilter();
        nested.setFirstName(termExact(List.of("nested-all"), List.of("nested-any"), "nested-eq", List.of("nested-in")));

        PersonBaseFilter filter = new PersonBaseFilter();
        filter.setFullName(termFullText(List.of("alpha", "beta"), List.of("gamma"), "all-text", "any-text"));
        filter.setFirstName(termExact(List.of("exact-a", "exact-b"), List.of("exact-any"), "exact-eq", List.of("in-1", "in-2")));
        filter.setLastName(termExact(List.of("last-a"), null, "last-eq", null));
        filter.setAnd(List.of(nested));
        filter.setOr(List.of(nested));
        filter.setNot(nested);
        return filter;
    }

    public static EmailBaseFilter emailBase(String eq) {
        EmailBaseFilter nested = new EmailBaseFilter();
        nested.setEmail(regexpExact("nested-re.*", "nested-" + eq, null));

        EmailBaseFilter filter = new EmailBaseFilter();
        filter.setEmail(regexpExact("re.*", eq, List.of(eq + "-1", eq + "-2")));
        filter.setAnd(List.of(nested));
        filter.setOr(List.of(nested));
        filter.setNot(nested);
        return filter;
    }

    public static AddressBaseFilter address() {
        AddressBaseFilter nested = new AddressBaseFilter();
        nested.setAddress(regexpFullText("nested-re.*", null, null));

        AddressBaseFilter filter = new AddressBaseFilter();
        filter.setAddress(regexpFullText("re.*", "all-text", "any-text"));
        filter.setZip(exact("zip-1", List.of("zip-2", "zip-3")));
        filter.setCountry(exact("RU", null));
        filter.setRegion(exact("MSK", null));
        filter.setCity(exact("Moscow", null));
        filter.setAnd(List.of(nested));
        filter.setOr(List.of(nested));
        filter.setNot(nested);
        return filter;
    }

    public static InnBaseFilter inn() {
        InnBaseFilter nested = new InnBaseFilter();
        nested.setInn(termExact(List.of("nested-inn-a"), null, "nested-inn", null));

        InnBaseFilter filter = new InnBaseFilter();
        filter.setInn(termExact(List.of("inn-a", "inn-b"), List.of("inn-any"), "inn-eq", List.of("inn-in-1")));
        filter.setAnd(List.of(nested));
        filter.setOr(List.of(nested));
        filter.setNot(nested);
        return filter;
    }

    public static MsisdnBaseFilter msisdn() {
        MsisdnBaseFilter nested = new MsisdnBaseFilter();
        nested.setMsisdn(regexpExact("nested-79.*", "nested-msisdn", null));

        MsisdnBaseFilter filter = new MsisdnBaseFilter();
        filter.setMsisdn(regexpExact("79.*", "79990001122", List.of("79990001133")));
        filter.setAnd(List.of(nested));
        filter.setOr(List.of(nested));
        filter.setNot(nested);
        return filter;
    }

    public static MccBaseFilter mcc() {
        MccBaseFilter nested = new MccBaseFilter();
        nested.setMcc(intCondition(999, null, null, null, null, null, null));

        MccBaseFilter filter = new MccBaseFilter();
        filter.setMcc(intCondition(250, List.of(251, 252), 253, 254, 255, 256, intRange(1, 999)));
        filter.setAnd(List.of(nested));
        filter.setOr(List.of(nested));
        filter.setNot(nested);
        return filter;
    }

    public static OrganizationInfoBaseFilter organizationInfo() {
        OrganizationInfoBaseFilter nested = new OrganizationInfoBaseFilter();
        nested.setNameFull(regexpExact("Nested.*", "Nested", null));

        OrganizationInfoBaseFilter filter = new OrganizationInfoBaseFilter();
        filter.setNameFull(regexpExact("Grunvald.*", "Grunvald", List.of("Other")));
        filter.setAnd(List.of(nested));
        filter.setOr(List.of(nested));
        filter.setNot(nested);
        return filter;
    }

    public static IpAddressBaseFilter ipAddress() {
        IpAddressBaseFilter nested = new IpAddressBaseFilter();
        nested.setIp(regexpExact("fe80.*", "fe80::1", null));
        nested.setType(IpType.IPV6);

        IpAddressBaseFilter filter = new IpAddressBaseFilter();
        filter.setIp(regexpExact("10\\.0\\..*", "10.0.0.1", List.of("10.0.0.2", "10.0.0.3")));
        filter.setType(IpType.IPV4);
        filter.setAnd(List.of(nested));
        filter.setOr(List.of(nested));
        filter.setNot(nested);
        return filter;
    }

    public static LoginBaseFilter loginBase() {
        LoginBaseFilter nested = new LoginBaseFilter();
        nested.setLogin(regexpExact("nested-user.*", "nested-login", null));

        LoginBaseFilter filter = new LoginBaseFilter();
        filter.setLogin(regexpExact("user.*", "user-1", List.of("user-2")));
        filter.setAnd(List.of(nested));
        filter.setOr(List.of(nested));
        filter.setNot(nested);
        return filter;
    }

    public static DomainNameBaseFilter domainName(String eq) {
        DomainNameBaseFilter nested = new DomainNameBaseFilter();
        nested.setDomainName(regexpExact("nested\\..*", "nested.ru", null));

        DomainNameBaseFilter filter = new DomainNameBaseFilter();
        filter.setDomainName(regexpExact(".*\\.ru", eq, List.of("b.ru")));
        filter.setAnd(List.of(nested));
        filter.setOr(List.of(nested));
        filter.setNot(nested);
        return filter;
    }

    // ===== entity filters =====

    public static UserFilter user() {
        UserFilter child = new UserFilter();
        child.setEmail(emailBase("nested-user@mail.com"));

        UserFilter filter = new UserFilter();
        filter.setName(person());
        filter.setEmail(emailBase("user@mail.com"));
        filter.setAnd(List.of(child));
        filter.setOr(List.of(child));
        filter.setNot(child);
        return filter;
    }

    public static ClientGroupFilter clientGroup() {
        ClientGroupFilter child = new ClientGroupFilter();
        child.setUpdatedAt(dateTime(D02));

        ClientGroupFilter filter = new ClientGroupFilter();
        filter.setUpdatedAt(dateTime(D01));
        filter.setAnd(List.of(child));
        filter.setOr(List.of(child));
        filter.setNot(child);
        return filter;
    }

    public static ClientFilter client() {
        ClientFilter child = new ClientFilter();
        child.setEmail(emailBase("nested-client@mail.com"));

        ClientFilter filter = new ClientFilter();
        filter.setName(person());
        filter.setUserOwner(user());
        filter.setClientGroup(clientGroup());
        filter.setOrganizationInfo(organizationInfo());
        filter.setInn(inn());
        filter.setEmail(emailBase("client@mail.com"));
        filter.setMsisdn(msisdn());
        filter.setMcc(mcc());
        filter.setAddress(address());
        filter.setCreatedAt(dateTime(D01));
        filter.setAnd(List.of(child));
        filter.setOr(List.of(child));
        filter.setNot(child);
        return filter;
    }

    public static OrderItemFilter orderItem() {
        OrderItemFilter child = new OrderItemFilter();
        child.setDomain(domainName("child.ru"));

        OrderItemFilter filter = new OrderItemFilter();
        filter.setDomain(domainName("a.ru"));
        filter.setAnd(List.of(child));
        filter.setOr(List.of(child));
        filter.setNot(child);
        return filter;
    }

    public static InvoiceFilter invoice() {
        InvoiceFilter child = new InvoiceFilter();
        child.setDate(dateTime(D08));

        InvoiceFilter filter = new InvoiceFilter();
        filter.setClient(client());
        filter.setDate(dateTime(D01));
        filter.setDueDate(dateTime(D02));
        filter.setDatePaid(dateTime(D03));
        filter.setDateRefunded(dateTime(D04));
        filter.setDateCancelled(dateTime(D05));
        filter.setCreatedAt(dateTime(D06));
        filter.setUpdatedAt(dateTime(D07));
        filter.setAnd(List.of(child));
        filter.setOr(List.of(child));
        filter.setNot(child);
        return filter;
    }

    public static OrderFilter order() {
        OrderFilter child = new OrderFilter();
        child.setDate(dateTime(D09));

        OrderFilter filter = new OrderFilter();
        filter.setClient(client());
        filter.setInvoice(invoice());
        filter.setItems(orderItem());
        filter.setIpAddress(ipAddress());
        filter.setDate(dateTime(D01));
        filter.setAnd(List.of(child));
        filter.setOr(List.of(child));
        filter.setNot(child);
        return filter;
    }

    public static AffiliateFilter affiliate() {
        AffiliateFilter child = new AffiliateFilter();
        child.setDate(dateTime(D04));

        AffiliateFilter filter = new AffiliateFilter();
        filter.setClient(client());
        filter.setDate(dateTime(D01));
        filter.setCreatedAt(dateTime(D02));
        filter.setUpdatedAt(dateTime(D03));
        filter.setAnd(List.of(child));
        filter.setOr(List.of(child));
        filter.setNot(child);
        return filter;
    }

    public static ActivityLogFilter activityLog() {
        ActivityLogFilter child = new ActivityLogFilter();
        child.setDate(dateTime(D06));

        ActivityLogFilter filter = new ActivityLogFilter();
        filter.setClient(client());
        filter.setUser(user());
        filter.setUsername(loginBase());
        filter.setIpAddress(ipAddress());
        filter.setDate(dateTime(D05));
        filter.setAnd(List.of(child));
        filter.setOr(List.of(child));
        filter.setNot(child);
        return filter;
    }

    public static ContactFilter contact() {
        ContactFilter child = new ContactFilter();
        child.setUpdatedAt(dateTime(D09));

        ContactFilter filter = new ContactFilter();
        filter.setClient(client());
        filter.setName(person());
        filter.setOrganizationInfo(organizationInfo());
        filter.setEmail(emailBase("contact@mail.com"));
        filter.setAddress(address());
        filter.setInn(inn());
        filter.setMsisdn(msisdn());
        filter.setUpdatedAt(dateTime(D07));
        filter.setCreatedAt(dateTime(D08));
        filter.setAnd(List.of(child));
        filter.setOr(List.of(child));
        filter.setNot(child);
        return filter;
    }

    public static CreditFilter credit() {
        CreditFilter child = new CreditFilter();
        child.setDate(dateTime(D02));

        CreditFilter filter = new CreditFilter();
        filter.setClient(client());
        filter.setDate(dateTime(D01));
        filter.setAnd(List.of(child));
        filter.setOr(List.of(child));
        filter.setNot(child);
        return filter;
    }

    public static EmailFilter email() {
        EmailFilter child = new EmailFilter();
        child.setTo(emailBase("child-to@mail.com"));

        EmailFilter filter = new EmailFilter();
        filter.setClient(client());
        filter.setTo(emailBase("to@mail.com"));
        filter.setCc(emailBase("cc@mail.com"));
        filter.setBcc(emailBase("bcc@mail.com"));
        filter.setDate(dateTime(D01));
        filter.setCreatedAt(dateTime(D02));
        filter.setUpdatedAt(dateTime(D03));
        filter.setAnd(List.of(child));
        filter.setOr(List.of(child));
        filter.setNot(child);
        return filter;
    }

    public static PaymentMethodFilter paymentMethod() {
        PaymentMethodFilter child = new PaymentMethodFilter();
        child.setUpdatedAt(dateTime(D02));

        PaymentMethodFilter filter = new PaymentMethodFilter();
        filter.setUpdatedAt(dateTime(D01));
        filter.setAnd(List.of(child));
        filter.setOr(List.of(child));
        filter.setNot(child);
        return filter;
    }

    public static ProductFilter product() {
        ProductFilter child = new ProductFilter();
        child.setUpdatedAt(dateTime(D02));

        ProductFilter filter = new ProductFilter();
        filter.setUpdatedAt(dateTime(D01));
        filter.setAnd(List.of(child));
        filter.setOr(List.of(child));
        filter.setNot(child);
        return filter;
    }

    public static TransactionFilter transaction() {
        TransactionFilter child = new TransactionFilter();
        child.setDate(dateTime(D09));

        TransactionFilter filter = new TransactionFilter();
        filter.setInvoice(invoice());
        filter.setClient(client());
        filter.setDate(dateTime(D08));
        filter.setAnd(List.of(child));
        filter.setOr(List.of(child));
        filter.setNot(child);
        return filter;
    }

    // ===== condition builders =====

    private static StringTermFullTextCondition termFullText(List<String> allOfTerms, List<String> anyOfTerms,
                                                            String allOfText, String anyOfText) {
        StringTermFullTextCondition condition = new StringTermFullTextCondition();
        condition.setAllOfTerms(allOfTerms);
        condition.setAnyOfTerms(anyOfTerms);
        condition.setAllOfText(allOfText);
        condition.setAnyOfText(anyOfText);
        return condition;
    }

    private static StringTermExactCondition termExact(List<String> allOfTerms, List<String> anyOfTerms,
                                                      String eq, List<String> in) {
        StringTermExactCondition condition = new StringTermExactCondition();
        condition.setAllOfTerms(allOfTerms);
        condition.setAnyOfTerms(anyOfTerms);
        condition.setEq(eq);
        condition.setIn(in);
        return condition;
    }

    private static StringRegExpExactCondition regexpExact(String regexp, String eq, List<String> in) {
        StringRegExpExactCondition condition = new StringRegExpExactCondition();
        condition.setRegexp(regexp);
        condition.setEq(eq);
        condition.setIn(in);
        return condition;
    }

    private static StringRegExpFullTextCondition regexpFullText(String regexp, String allOfText, String anyOfText) {
        StringRegExpFullTextCondition condition = new StringRegExpFullTextCondition();
        condition.setRegexp(regexp);
        condition.setAllOfText(allOfText);
        condition.setAnyOfText(anyOfText);
        return condition;
    }

    private static StringExactCondition exact(String eq, List<String> in) {
        StringExactCondition condition = new StringExactCondition();
        condition.setEq(eq);
        condition.setIn(in);
        return condition;
    }

    private static IntCondition intCondition(Integer eq, List<Integer> in, Integer le, Integer lt,
                                             Integer ge, Integer gt, IntRangeCondition between) {
        IntCondition condition = new IntCondition();
        condition.setEq(eq);
        condition.setIn(in);
        condition.setLe(le);
        condition.setLt(lt);
        condition.setGe(ge);
        condition.setGt(gt);
        condition.setBetween(between);
        return condition;
    }

    private static IntRangeCondition intRange(Integer min, Integer max) {
        IntRangeCondition range = new IntRangeCondition();
        range.setMin(min);
        range.setMax(max);
        return range;
    }

    // ===== assertions for mapped persistence base filters =====
    //
    // Persistence base filters expose no getters, so their fields are read reflectively
    // and checked against the values set by the corresponding domain sample builders.

    @SuppressWarnings("unchecked")
    public static void assertDateTime(
            com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter filter,
            OffsetDateTime eq) {
        assertNotNull(filter);
        com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.DateTimeCondition utc =
                (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.DateTimeCondition) read(filter, "utc");
        assertEquals(eq, utc.eq());
        assertEquals(List.of(eq.plusDays(1), eq.plusDays(2)), utc.in());
        assertEquals(eq.plusDays(3), utc.le());
        assertEquals(eq.plusDays(4), utc.lt());
        assertEquals(eq.plusDays(5), utc.ge());
        assertEquals(eq.plusDays(6), utc.gt());
        assertEquals(eq.plusDays(7), utc.between().min());
        assertEquals(eq.plusDays(8), utc.between().max());

        var and = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter>) read(filter, "and");
        var or = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter>) read(filter, "or");
        var not = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter) read(filter, "not");
        assertEquals(1, and.size());
        assertNestedDateTimeEq(and.get(0), eq.plusDays(30));
        assertEquals(1, or.size());
        assertNestedDateTimeEq(or.get(0), eq.plusDays(30));
        assertNestedDateTimeEq(not, eq.plusDays(30));
    }

    private static void assertNestedDateTimeEq(
            com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter filter,
            OffsetDateTime eq) {
        var utc = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.DateTimeCondition) read(filter, "utc");
        assertEquals(eq, utc.eq());
    }

    @SuppressWarnings("unchecked")
    public static void assertPerson(com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.PersonBaseFilter filter) {
        assertNotNull(filter);
        var fullName = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermFullTextCondition)
                read(filter, "fullName");
        assertEquals(List.of("alpha", "beta"), fullName.allOfTerms());
        assertEquals(List.of("gamma"), fullName.anyOfTerms());
        assertEquals("all-text", fullName.allOfText());
        assertEquals("any-text", fullName.anyOfText());

        var firstName = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermExactCondition)
                read(filter, "firstName");
        assertEquals(List.of("exact-a", "exact-b"), firstName.allOfTerms());
        assertEquals(List.of("exact-any"), firstName.anyOfTerms());
        assertEquals("exact-eq", firstName.eq());
        assertEquals(List.of("in-1", "in-2"), firstName.in());

        var lastName = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermExactCondition)
                read(filter, "lastName");
        assertEquals(List.of("last-a"), lastName.allOfTerms());
        assertNull(lastName.anyOfTerms());
        assertEquals("last-eq", lastName.eq());
        assertNull(lastName.in());

        var and = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.PersonBaseFilter>) read(filter, "and");
        assertEquals(1, and.size());
        var nestedFirstName = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermExactCondition)
                read(and.get(0), "firstName");
        assertEquals("nested-eq", nestedFirstName.eq());
        assertEquals(List.of("nested-in"), nestedFirstName.in());
        var or = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.PersonBaseFilter>) read(filter, "or");
        assertEquals(1, or.size());
        var not = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.PersonBaseFilter) read(filter, "not");
        assertNotNull(not);
    }

    @SuppressWarnings("unchecked")
    public static void assertEmail(
            com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.EmailBaseFilter filter, String eq) {
        assertNotNull(filter);
        var email = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition)
                read(filter, "email");
        assertEquals("re.*", email.regexp());
        assertEquals(eq, email.eq());
        assertEquals(List.of(eq + "-1", eq + "-2"), email.in());

        var and = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.EmailBaseFilter>) read(filter, "and");
        assertEquals(1, and.size());
        var nestedEmail = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition)
                read(and.get(0), "email");
        assertEquals("nested-re.*", nestedEmail.regexp());
        assertEquals("nested-" + eq, nestedEmail.eq());
        var or = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.EmailBaseFilter>) read(filter, "or");
        assertEquals(1, or.size());
        var not = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.EmailBaseFilter) read(filter, "not");
        assertNotNull(not);
    }

    @SuppressWarnings("unchecked")
    public static void assertAddress(com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.AddressBaseFilter filter) {
        assertNotNull(filter);
        var address = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpFullTextCondition)
                read(filter, "address");
        assertEquals("re.*", address.regexp());
        assertEquals("all-text", address.allOfText());
        assertEquals("any-text", address.anyOfText());

        var zip = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringExactCondition) read(filter, "zip");
        assertEquals("zip-1", zip.eq());
        assertEquals(List.of("zip-2", "zip-3"), zip.in());

        assertEquals("RU", ((com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringExactCondition)
                read(filter, "country")).eq());
        assertEquals("MSK", ((com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringExactCondition)
                read(filter, "region")).eq());
        assertEquals("Moscow", ((com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringExactCondition)
                read(filter, "city")).eq());

        var and = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.AddressBaseFilter>) read(filter, "and");
        assertEquals(1, and.size());
        var nestedAddress = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpFullTextCondition)
                read(and.get(0), "address");
        assertEquals("nested-re.*", nestedAddress.regexp());
        var or = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.AddressBaseFilter>) read(filter, "or");
        assertEquals(1, or.size());
        var not = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.AddressBaseFilter) read(filter, "not");
        assertNotNull(not);
    }

    @SuppressWarnings("unchecked")
    public static void assertInn(com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.InnBaseFilter filter) {
        assertNotNull(filter);
        var inn = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermExactCondition) read(filter, "inn");
        assertEquals(List.of("inn-a", "inn-b"), inn.allOfTerms());
        assertEquals(List.of("inn-any"), inn.anyOfTerms());
        assertEquals("inn-eq", inn.eq());
        assertEquals(List.of("inn-in-1"), inn.in());

        var and = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.InnBaseFilter>) read(filter, "and");
        assertEquals(1, and.size());
        assertEquals("nested-inn", ((com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermExactCondition)
                read(and.get(0), "inn")).eq());
        var or = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.InnBaseFilter>) read(filter, "or");
        assertEquals(1, or.size());
        assertNotNull(read(filter, "not"));
    }

    @SuppressWarnings("unchecked")
    public static void assertMsisdn(com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.MsisdnBaseFilter filter) {
        assertNotNull(filter);
        var msisdn = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition)
                read(filter, "msisdn");
        assertEquals("79.*", msisdn.regexp());
        assertEquals("79990001122", msisdn.eq());
        assertEquals(List.of("79990001133"), msisdn.in());

        var and = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.MsisdnBaseFilter>) read(filter, "and");
        assertEquals(1, and.size());
        assertEquals("nested-msisdn", ((com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition)
                read(and.get(0), "msisdn")).eq());
        var or = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.MsisdnBaseFilter>) read(filter, "or");
        assertEquals(1, or.size());
        assertNotNull(read(filter, "not"));
    }

    @SuppressWarnings("unchecked")
    public static void assertMcc(com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.MccBaseFilter filter) {
        assertNotNull(filter);
        var mcc = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.IntCondition) read(filter, "mcc");
        assertEquals(250, mcc.eq());
        assertEquals(List.of(251, 252), mcc.in());
        assertEquals(253, mcc.le());
        assertEquals(254, mcc.lt());
        assertEquals(255, mcc.ge());
        assertEquals(256, mcc.gt());
        assertEquals(1, mcc.between().min());
        assertEquals(999, mcc.between().max());

        var and = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.MccBaseFilter>) read(filter, "and");
        assertEquals(1, and.size());
        assertEquals(999, ((com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.IntCondition)
                read(and.get(0), "mcc")).eq());
        var or = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.MccBaseFilter>) read(filter, "or");
        assertEquals(1, or.size());
        assertNotNull(read(filter, "not"));
    }

    @SuppressWarnings("unchecked")
    public static void assertOrganizationInfo(
            com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.OrganizationInfoBaseFilter filter) {
        assertNotNull(filter);
        var nameFull = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition)
                read(filter, "nameFull");
        assertEquals("Grunvald.*", nameFull.regexp());
        assertEquals("Grunvald", nameFull.eq());
        assertEquals(List.of("Other"), nameFull.in());

        var and = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.OrganizationInfoBaseFilter>) read(filter, "and");
        assertEquals(1, and.size());
        assertEquals("Nested", ((com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition)
                read(and.get(0), "nameFull")).eq());
        var or = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.OrganizationInfoBaseFilter>) read(filter, "or");
        assertEquals(1, or.size());
        assertNotNull(read(filter, "not"));
    }

    @SuppressWarnings("unchecked")
    public static void assertIpAddress(com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.IpAddressBaseFilter filter) {
        assertNotNull(filter);
        var ip = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition) read(filter, "ip");
        assertEquals("10\\.0\\..*", ip.regexp());
        assertEquals("10.0.0.1", ip.eq());
        assertEquals(List.of("10.0.0.2", "10.0.0.3"), ip.in());
        assertEquals(IpType.IPV4, read(filter, "type"));

        var and = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.IpAddressBaseFilter>) read(filter, "and");
        assertEquals(1, and.size());
        var nested = and.get(0);
        assertEquals("fe80::1", ((com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition)
                read(nested, "ip")).eq());
        assertEquals(IpType.IPV6, read(nested, "type"));
        var or = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.IpAddressBaseFilter>) read(filter, "or");
        assertEquals(1, or.size());
        assertNotNull(read(filter, "not"));
    }

    @SuppressWarnings("unchecked")
    public static void assertLogin(com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.LoginFilter filter) {
        assertNotNull(filter);
        var login = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition)
                read(filter, "login");
        assertEquals("user.*", login.regexp());
        assertEquals("user-1", login.eq());
        assertEquals(List.of("user-2"), login.in());

        var and = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.LoginFilter>) read(filter, "and");
        assertEquals(1, and.size());
        assertEquals("nested-login", ((com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition)
                read(and.get(0), "login")).eq());
        var or = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.LoginFilter>) read(filter, "or");
        assertEquals(1, or.size());
        assertNotNull(read(filter, "not"));
    }

    @SuppressWarnings("unchecked")
    public static void assertDomainName(
            com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DomainNameBaseFilter filter, String eq) {
        assertNotNull(filter);
        var domainName = (com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition)
                read(filter, "domainName");
        assertEquals(".*\\.ru", domainName.regexp());
        assertEquals(eq, domainName.eq());
        assertEquals(List.of("b.ru"), domainName.in());

        var and = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DomainNameBaseFilter>) read(filter, "and");
        assertEquals(1, and.size());
        assertEquals("nested.ru", ((com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition)
                read(and.get(0), "domainName")).eq());
        var or = (List<com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DomainNameBaseFilter>) read(filter, "or");
        assertEquals(1, or.size());
        assertNotNull(read(filter, "not"));
    }
}
