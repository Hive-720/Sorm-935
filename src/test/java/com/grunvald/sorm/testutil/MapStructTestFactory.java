package com.grunvald.sorm.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.infrastructure.persistence.data.activitylog.entity.mapper.ActivityLogPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.activitylog.entity.mapper.ActivityLogPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.affiliate.entity.mapper.AffiliatePersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.affiliate.entity.mapper.AffiliatePersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.client.entity.mapper.ClientGroupPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.client.entity.mapper.ClientGroupPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.client.entity.mapper.ClientLoginMapper;
import com.grunvald.sorm.infrastructure.persistence.data.client.entity.mapper.ClientLoginMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.client.entity.mapper.ClientPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.client.entity.mapper.ClientPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.client.entity.mapper.ClientProductPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.client.entity.mapper.ClientProductPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.contact.entity.mapper.ContactPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.contact.entity.mapper.ContactPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.credit.entity.mapper.CreditPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.credit.entity.mapper.CreditPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.email.entity.mapper.EmailPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.email.entity.mapper.EmailPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.invoice.entity.mapper.InvoicePersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.invoice.entity.mapper.InvoicePersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.order.entity.mapper.OrderItemPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.order.entity.mapper.OrderItemPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.order.entity.mapper.OrderPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.order.entity.mapper.OrderPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.paymentmethod.entity.mapper.PaymentMethodPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.paymentmethod.entity.mapper.PaymentMethodPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper.ProductConfigOptionPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper.ProductConfigOptionPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper.ProductConfigPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper.ProductConfigPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper.ProductConfigRelationPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper.ProductConfigRelationPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper.ProductPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.product.entity.mapper.ProductPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.transaction.entity.mapper.TransactionPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.transaction.entity.mapper.TransactionPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.user.entity.mapper.UserClientRelationPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.user.entity.mapper.UserClientRelationPersistenceMapperImpl;
import com.grunvald.sorm.infrastructure.persistence.data.user.entity.mapper.UserPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.user.entity.mapper.UserPersistenceMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ActivityLogPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ActivityLogPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.AffiliatePresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.AffiliatePresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientGroupPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientGroupPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientLoginPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientLoginPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientProductPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ClientProductPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ContactPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ContactPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.CreditPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.CreditPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.EmailPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.EmailPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.InvoicePresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.InvoicePresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.OrderItemPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.OrderItemPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.OrderPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.OrderPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.PaymentMethodPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.PaymentMethodPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.PricePresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.PricePresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ProductConfigOptionPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ProductConfigOptionPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ProductConfigPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ProductConfigPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ProductPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.ProductPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.TransactionPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.TransactionPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.TrapMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.TrapMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.UserPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.UserPresentationMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ActivityLogResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ActivityLogResultMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.AffiliateResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.AffiliateResultMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ClientGroupResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ClientGroupResultMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ClientLoginResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ClientLoginResultMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ClientProductResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ClientProductResultMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ClientResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ClientResultMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ContactResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ContactResultMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.CreditResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.CreditResultMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.EmailResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.EmailResultMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.InvoiceResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.InvoiceResultMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.OrderResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.OrderResultMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.PaymentMethodResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.PaymentMethodResultMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ProductResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.ProductResultMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.TransactionResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.TransactionResultMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.UserResultMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper.UserResultMapperImpl;
import com.grunvald.sorm.presentation.graphqlapi.util.MapperOffsetDateTimeConverter;
import com.grunvald.sorm.presentation.graphqlapi.util.MapperTypeConverter;

import java.lang.reflect.Field;

/**
 * Создаёт реальные MapStruct Impl-классы без Spring-контекста.
 * <p>
 * Генерируемые MapStruct реализации используют инъекцию полей (@Autowired private ...),
 * поэтому зависимости внедряются через рефлексию. Для абстрактных персистенс-мапперов
 * поле {@code objectMapper} объявлено как protected в суперклассе — оно также
 * устанавливается через рефлексию.
 */
public final class MapStructTestFactory {

    private MapStructTestFactory() {
    }

    public static void inject(Object target, String fieldName, Object value) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Не удалось внедрить поле '" + fieldName + "' в " + target.getClass().getName(), e);
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

    public static MapperTypeConverter typeConverter() {
        return new MapperTypeConverter(new MapperOffsetDateTimeConverter());
    }

    private static ObjectMapper newObjectMapper() {
        return new ObjectMapper();
    }

    // ===== Part 1: presentation type mappers =====

    public static ActivityLogPresentationMapper activityLogPresentationMapper() {
        ActivityLogPresentationMapperImpl impl = new ActivityLogPresentationMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static AffiliatePresentationMapper affiliatePresentationMapper() {
        AffiliatePresentationMapperImpl impl = new AffiliatePresentationMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static ClientGroupPresentationMapper clientGroupPresentationMapper() {
        ClientGroupPresentationMapperImpl impl = new ClientGroupPresentationMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static ClientLoginPresentationMapper clientLoginPresentationMapper() {
        ClientLoginPresentationMapperImpl impl = new ClientLoginPresentationMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static ClientPresentationMapper clientPresentationMapper() {
        ClientPresentationMapperImpl impl = new ClientPresentationMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static ClientProductPresentationMapper clientProductPresentationMapper() {
        ClientProductPresentationMapperImpl impl = new ClientProductPresentationMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static ContactPresentationMapper contactPresentationMapper() {
        ContactPresentationMapperImpl impl = new ContactPresentationMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static CreditPresentationMapper creditPresentationMapper() {
        CreditPresentationMapperImpl impl = new CreditPresentationMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static EmailPresentationMapper emailPresentationMapper() {
        EmailPresentationMapperImpl impl = new EmailPresentationMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static InvoicePresentationMapper invoicePresentationMapper() {
        InvoicePresentationMapperImpl impl = new InvoicePresentationMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static OrderItemPresentationMapper orderItemPresentationMapper() {
        OrderItemPresentationMapperImpl impl = new OrderItemPresentationMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static OrderPresentationMapper orderPresentationMapper() {
        OrderPresentationMapperImpl impl = new OrderPresentationMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static PaymentMethodPresentationMapper paymentMethodPresentationMapper() {
        PaymentMethodPresentationMapperImpl impl = new PaymentMethodPresentationMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static PricePresentationMapper pricePresentationMapper() {
        return new PricePresentationMapperImpl();
    }

    public static ProductConfigOptionPresentationMapper productConfigOptionPresentationMapper() {
        ProductConfigOptionPresentationMapperImpl impl = new ProductConfigOptionPresentationMapperImpl();
        inject(impl, "pricePresentationMapper", pricePresentationMapper());
        return impl;
    }

    public static ProductConfigPresentationMapper productConfigPresentationMapper() {
        return new ProductConfigPresentationMapperImpl();
    }

    public static ProductPresentationMapper productPresentationMapper() {
        ProductPresentationMapperImpl impl = new ProductPresentationMapperImpl();
        inject(impl, "pricePresentationMapper", pricePresentationMapper());
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static TransactionPresentationMapper transactionPresentationMapper() {
        TransactionPresentationMapperImpl impl = new TransactionPresentationMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static TrapMapper trapMapper() {
        TrapMapperImpl impl = new TrapMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    public static UserPresentationMapper userPresentationMapper() {
        UserPresentationMapperImpl impl = new UserPresentationMapperImpl();
        inject(impl, "mapperTypeConverter", typeConverter());
        return impl;
    }

    // ===== Part 2: presentation result mappers =====

    public static ActivityLogResultMapper activityLogResultMapper() {
        ActivityLogResultMapperImpl impl = new ActivityLogResultMapperImpl();
        inject(impl, "activityLogPresentationMapper", activityLogPresentationMapper());
        return impl;
    }

    public static AffiliateResultMapper affiliateResultMapper() {
        AffiliateResultMapperImpl impl = new AffiliateResultMapperImpl();
        inject(impl, "affiliatePresentationMapper", affiliatePresentationMapper());
        return impl;
    }

    public static ClientGroupResultMapper clientGroupResultMapper() {
        ClientGroupResultMapperImpl impl = new ClientGroupResultMapperImpl();
        inject(impl, "clientGroupPresentationMapper", clientGroupPresentationMapper());
        return impl;
    }

    public static ClientLoginResultMapper clientLoginResultMapper() {
        ClientLoginResultMapperImpl impl = new ClientLoginResultMapperImpl();
        inject(impl, "clientLoginPresentationMapper", clientLoginPresentationMapper());
        return impl;
    }

    public static ClientProductResultMapper clientProductResultMapper() {
        ClientProductResultMapperImpl impl = new ClientProductResultMapperImpl();
        inject(impl, "clientProductPresentationMapper", clientProductPresentationMapper());
        return impl;
    }

    public static ClientResultMapper clientResultMapper() {
        ClientResultMapperImpl impl = new ClientResultMapperImpl();
        inject(impl, "clientPresentationMapper", clientPresentationMapper());
        return impl;
    }

    public static ContactResultMapper contactResultMapper() {
        ContactResultMapperImpl impl = new ContactResultMapperImpl();
        inject(impl, "contactPresentationMapper", contactPresentationMapper());
        return impl;
    }

    public static CreditResultMapper creditResultMapper() {
        CreditResultMapperImpl impl = new CreditResultMapperImpl();
        inject(impl, "creditPresentationMapper", creditPresentationMapper());
        return impl;
    }

    public static EmailResultMapper emailResultMapper() {
        EmailResultMapperImpl impl = new EmailResultMapperImpl();
        inject(impl, "emailPresentationMapper", emailPresentationMapper());
        return impl;
    }

    public static InvoiceResultMapper invoiceResultMapper() {
        InvoiceResultMapperImpl impl = new InvoiceResultMapperImpl();
        inject(impl, "invoicePresentationMapper", invoicePresentationMapper());
        return impl;
    }

    public static OrderResultMapper orderResultMapper() {
        OrderResultMapperImpl impl = new OrderResultMapperImpl();
        inject(impl, "orderPresentationMapper", orderPresentationMapper());
        return impl;
    }

    public static PaymentMethodResultMapper paymentMethodResultMapper() {
        PaymentMethodResultMapperImpl impl = new PaymentMethodResultMapperImpl();
        inject(impl, "paymentMethodPresentationMapper", paymentMethodPresentationMapper());
        return impl;
    }

    public static ProductResultMapper productResultMapper() {
        ProductResultMapperImpl impl = new ProductResultMapperImpl();
        inject(impl, "productPresentationMapper", productPresentationMapper());
        return impl;
    }

    public static TransactionResultMapper transactionResultMapper() {
        TransactionResultMapperImpl impl = new TransactionResultMapperImpl();
        inject(impl, "transactionPresentationMapper", transactionPresentationMapper());
        return impl;
    }

    public static UserResultMapper userResultMapper() {
        UserResultMapperImpl impl = new UserResultMapperImpl();
        inject(impl, "userPresentationMapper", userPresentationMapper());
        return impl;
    }

    // ===== Part 3: persistence mappers =====

    public static ActivityLogPersistenceMapper activityLogPersistenceMapper() {
        return new ActivityLogPersistenceMapperImpl();
    }

    public static AffiliatePersistenceMapper affiliatePersistenceMapper() {
        return new AffiliatePersistenceMapperImpl();
    }

    public static ClientGroupPersistenceMapper clientGroupPersistenceMapper() {
        return new ClientGroupPersistenceMapperImpl();
    }

    public static ClientLoginMapper clientLoginMapper() {
        return new ClientLoginMapperImpl();
    }

    public static ClientPersistenceMapper clientPersistenceMapper() {
        return new ClientPersistenceMapperImpl();
    }

    public static ClientProductPersistenceMapper clientProductPersistenceMapper() {
        ClientProductPersistenceMapperImpl impl = new ClientProductPersistenceMapperImpl();
        inject(impl, "objectMapper", newObjectMapper());
        return impl;
    }

    public static ContactPersistenceMapper contactPersistenceMapper() {
        return new ContactPersistenceMapperImpl();
    }

    public static CreditPersistenceMapper creditPersistenceMapper() {
        return new CreditPersistenceMapperImpl();
    }

    public static EmailPersistenceMapper emailPersistenceMapper() {
        return new EmailPersistenceMapperImpl();
    }

    public static InvoicePersistenceMapper invoicePersistenceMapper() {
        InvoicePersistenceMapperImpl impl = new InvoicePersistenceMapperImpl();
        inject(impl, "objectMapper", newObjectMapper());
        return impl;
    }

    public static OrderItemPersistenceMapper orderItemPersistenceMapper() {
        return new OrderItemPersistenceMapperImpl();
    }

    public static OrderPersistenceMapper orderPersistenceMapper() {
        return new OrderPersistenceMapperImpl();
    }

    public static PaymentMethodPersistenceMapper paymentMethodPersistenceMapper() {
        return new PaymentMethodPersistenceMapperImpl();
    }

    public static ProductConfigOptionPersistenceMapper productConfigOptionPersistenceMapper() {
        ProductConfigOptionPersistenceMapperImpl impl = new ProductConfigOptionPersistenceMapperImpl();
        inject(impl, "objectMapper", newObjectMapper());
        return impl;
    }

    public static ProductConfigPersistenceMapper productConfigPersistenceMapper() {
        return new ProductConfigPersistenceMapperImpl();
    }

    public static ProductConfigRelationPersistenceMapper productConfigRelationPersistenceMapper() {
        return new ProductConfigRelationPersistenceMapperImpl();
    }

    public static ProductPersistenceMapper productPersistenceMapper() {
        ProductPersistenceMapperImpl impl = new ProductPersistenceMapperImpl();
        inject(impl, "objectMapper", newObjectMapper());
        return impl;
    }

    public static TransactionPersistenceMapper transactionPersistenceMapper() {
        return new TransactionPersistenceMapperImpl();
    }

    public static UserClientRelationPersistenceMapper userClientRelationPersistenceMapper() {
        return new UserClientRelationPersistenceMapperImpl();
    }

    public static UserPersistenceMapper userPersistenceMapper() {
        return new UserPersistenceMapperImpl();
    }
}
