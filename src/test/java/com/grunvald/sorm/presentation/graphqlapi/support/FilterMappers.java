package com.grunvald.sorm.presentation.graphqlapi.support;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.DateTimeRangePresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.DateTimeStringFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.StringRegExpFullTextFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.StringTermExactFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper.StringTermFullTextFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.AddressBaseFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.InnBaseFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.PersonBaseFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.ActivityLogFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.AffiliateFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.ClientFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.ClientGroupFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.ClientLoginFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.ClientProductFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.ContactFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.CreditFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.EmailFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.InvoiceFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.OrderFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.PaymentMethodFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.ProductFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.TransactionFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper.UserFilterPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.util.MapperOffsetDateTimeConverter;
import com.grunvald.sorm.presentation.graphqlapi.util.MapperTypeConverter;

import java.lang.reflect.Field;


public final class FilterMappers {

    public final MapperOffsetDateTimeConverter offsetDateTimeConverter = new MapperOffsetDateTimeConverter();
    public final MapperTypeConverter typeConverter = new MapperTypeConverter(offsetDateTimeConverter);

    public final DateTimeRangePresentationMapperImpl dateTimeRangeMapper = new DateTimeRangePresentationMapperImpl();
    public final DateTimeStringFilterPresentationMapperImpl dateTimeStringFilterMapper = new DateTimeStringFilterPresentationMapperImpl();
    public final StringRegExpFullTextFilterPresentationMapperImpl stringRegExpFullTextMapper = new StringRegExpFullTextFilterPresentationMapperImpl();
    public final StringTermExactFilterPresentationMapperImpl stringTermExactMapper = new StringTermExactFilterPresentationMapperImpl();
    public final StringTermFullTextFilterPresentationMapperImpl stringTermFullTextMapper = new StringTermFullTextFilterPresentationMapperImpl();

    public final AddressBaseFilterPresentationMapperImpl addressBaseMapper = new AddressBaseFilterPresentationMapperImpl();
    public final DateTimeBaseFilterPresentationMapperImpl dateTimeBaseMapper = new DateTimeBaseFilterPresentationMapperImpl();
    public final InnBaseFilterPresentationMapperImpl innBaseMapper = new InnBaseFilterPresentationMapperImpl();
    public final PersonBaseFilterPresentationMapperImpl personBaseMapper = new PersonBaseFilterPresentationMapperImpl();

    public final UserFilterPresentationMapperImpl userMapper = new UserFilterPresentationMapperImpl();
    public final ClientGroupFilterPresentationMapperImpl clientGroupMapper = new ClientGroupFilterPresentationMapperImpl();
    public final ClientFilterPresentationMapperImpl clientMapper = new ClientFilterPresentationMapperImpl();
    public final ClientLoginFilterPresentationMapperImpl clientLoginMapper = new ClientLoginFilterPresentationMapperImpl();
    public final CreditFilterPresentationMapperImpl creditMapper = new CreditFilterPresentationMapperImpl();
    public final EmailFilterPresentationMapperImpl emailMapper = new EmailFilterPresentationMapperImpl();
    public final InvoiceFilterPresentationMapperImpl invoiceMapper = new InvoiceFilterPresentationMapperImpl();
    public final AffiliateFilterPresentationMapperImpl affiliateMapper = new AffiliateFilterPresentationMapperImpl();
    public final ActivityLogFilterPresentationMapperImpl activityLogMapper = new ActivityLogFilterPresentationMapperImpl();
    public final OrderFilterPresentationMapperImpl orderMapper = new OrderFilterPresentationMapperImpl();
    public final ProductFilterPresentationMapperImpl productMapper = new ProductFilterPresentationMapperImpl();
    public final PaymentMethodFilterPresentationMapperImpl paymentMethodMapper = new PaymentMethodFilterPresentationMapperImpl();
    public final ClientProductFilterPresentationMapperImpl clientProductMapper = new ClientProductFilterPresentationMapperImpl();
    public final TransactionFilterPresentationMapperImpl transactionMapper = new TransactionFilterPresentationMapperImpl();
    public final ContactFilterPresentationMapperImpl contactMapper = new ContactFilterPresentationMapperImpl();

    public FilterMappers() {
        wire(dateTimeRangeMapper, "mapperOffsetDateTimeConverter", offsetDateTimeConverter);

        wire(dateTimeStringFilterMapper, "dateTimeRangePresentationMapper", dateTimeRangeMapper);
        wire(dateTimeStringFilterMapper, "mapperOffsetDateTimeConverter", offsetDateTimeConverter);

        wire(addressBaseMapper, "stringRegExpFullTextFilterPresentationMapper", stringRegExpFullTextMapper);
        wire(dateTimeBaseMapper, "dateTimeStringFilterPresentationMapper", dateTimeStringFilterMapper);
        wire(innBaseMapper, "stringTermExactFilterPresentationMapper", stringTermExactMapper);
        wire(personBaseMapper, "stringTermFullTextFilterPresentationMapper", stringTermFullTextMapper);
        wire(personBaseMapper, "stringTermExactFilterPresentationMapper", stringTermExactMapper);

        wire(userMapper, "personBaseFilterPresentationMapper", personBaseMapper);
        wire(clientGroupMapper, "dateTimeBaseFilterPresentationMapper", dateTimeBaseMapper);

        wire(clientMapper, "personBaseFilterPresentationMapper", personBaseMapper);
        wire(clientMapper, "userFilterPresentationMapper", userMapper);
        wire(clientMapper, "clientGroupFilterPresentationMapper", clientGroupMapper);
        wire(clientMapper, "innBaseFilterPresentationMapper", innBaseMapper);
        wire(clientMapper, "addressBaseFilterPresentationMapper", addressBaseMapper);
        wire(clientMapper, "dateTimeBaseFilterPresentationMapper", dateTimeBaseMapper);

        wire(clientLoginMapper, "clientFilterPresentationMapper", clientMapper);
        wire(clientLoginMapper, "dateTimeBaseFilterPresentationMapper", dateTimeBaseMapper);

        wire(creditMapper, "clientFilterPresentationMapper", clientMapper);
        wire(creditMapper, "dateTimeBaseFilterPresentationMapper", dateTimeBaseMapper);

        wire(emailMapper, "clientFilterPresentationMapper", clientMapper);
        wire(emailMapper, "dateTimeBaseFilterPresentationMapper", dateTimeBaseMapper);

        wire(invoiceMapper, "clientFilterPresentationMapper", clientMapper);
        wire(invoiceMapper, "dateTimeBaseFilterPresentationMapper", dateTimeBaseMapper);

        wire(affiliateMapper, "clientFilterPresentationMapper", clientMapper);
        wire(affiliateMapper, "dateTimeBaseFilterPresentationMapper", dateTimeBaseMapper);

        wire(activityLogMapper, "clientFilterPresentationMapper", clientMapper);
        wire(activityLogMapper, "dateTimeBaseFilterPresentationMapper", dateTimeBaseMapper);
        wire(activityLogMapper, "userFilterPresentationMapper", userMapper);

        wire(orderMapper, "clientFilterPresentationMapper", clientMapper);
        wire(orderMapper, "invoiceFilterPresentationMapper", invoiceMapper);
        wire(orderMapper, "dateTimeBaseFilterPresentationMapper", dateTimeBaseMapper);

        wire(productMapper, "dateTimeBaseFilterPresentationMapper", dateTimeBaseMapper);
        wire(paymentMethodMapper, "dateTimeBaseFilterPresentationMapper", dateTimeBaseMapper);

        wire(clientProductMapper, "clientFilterPresentationMapper", clientMapper);
        wire(clientProductMapper, "orderFilterPresentationMapper", orderMapper);
        wire(clientProductMapper, "productFilterPresentationMapper", productMapper);
        wire(clientProductMapper, "dateTimeBaseFilterPresentationMapper", dateTimeBaseMapper);

        wire(transactionMapper, "invoiceFilterPresentationMapper", invoiceMapper);
        wire(transactionMapper, "clientFilterPresentationMapper", clientMapper);
        wire(transactionMapper, "dateTimeBaseFilterPresentationMapper", dateTimeBaseMapper);

        wire(contactMapper, "clientFilterPresentationMapper", clientMapper);
        wire(contactMapper, "personBaseFilterPresentationMapper", personBaseMapper);
        wire(contactMapper, "addressBaseFilterPresentationMapper", addressBaseMapper);
        wire(contactMapper, "innBaseFilterPresentationMapper", innBaseMapper);
        wire(contactMapper, "dateTimeBaseFilterPresentationMapper", dateTimeBaseMapper);
    }

    private static void wire(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Cannot wire field '" + fieldName + "' on " + target.getClass().getName(), e);
        }
    }
}
