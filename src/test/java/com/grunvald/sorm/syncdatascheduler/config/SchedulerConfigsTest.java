package com.grunvald.sorm.syncdatascheduler.config;

import com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.FirstStart;
import com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode;
import com.grunvald.sorm.syncdatascheduler.config.impl.ActivityLogSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.impl.AffiliateSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.impl.ClientDetailSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.impl.ClientGroupSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.impl.ClientProductSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.impl.ClientSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.impl.ContactDeleteSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.impl.ContactSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.impl.CreditSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.impl.EmailSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.impl.InvoiceSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.impl.OrderSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.impl.PaymentMethodSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.impl.ProductSchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.config.impl.UserSchedulerPropertiesConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.NORMAL;
import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.ONLY_FIRST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchedulerConfigsTest {

    private static final List<Integer> DEFAULT_DELAYS = List.of(1000, 5000, 10000, 20000, 60000, 120000);

    private final List<SchedulerPropertiesConfig> configs = List.of(
            new ActivityLogSchedulerPropertiesConfig(),
            new AffiliateSchedulerPropertiesConfig(),
            new ClientDetailSchedulerPropertiesConfig(),
            new ClientGroupSchedulerPropertiesConfig(),
            new ClientProductSchedulerPropertiesConfig(),
            new ClientSchedulerPropertiesConfig(),
            new ContactDeleteSchedulerPropertiesConfig(),
            new ContactSchedulerPropertiesConfig(),
            new CreditSchedulerPropertiesConfig(),
            new EmailSchedulerPropertiesConfig(),
            new InvoiceSchedulerPropertiesConfig(),
            new OrderSchedulerPropertiesConfig(),
            new PaymentMethodSchedulerPropertiesConfig(),
            new ProductSchedulerPropertiesConfig(),
            new UserSchedulerPropertiesConfig()
    );

    @Test
    void allConfigs_haveBaseDefaults() {
        for (var config : configs) {
            assertFalse(config.isEnabled(), config.getClass().getSimpleName());
            assertNull(config.getCronExpression(), config.getClass().getSimpleName());
        }
    }

    @Test
    void allConfigs_haveDefaultModeAndFirstStart() {
        for (var config : configs) {
            if (config instanceof ContactDeleteSchedulerPropertiesConfig) {
                continue;
            }
            assertEquals(NORMAL, config.getMode(), config.getClass().getSimpleName());
            assertEquals(DEFAULT_DELAYS, config.getFirstStart().getDelays(), config.getClass().getSimpleName());
            assertTrue(config.getFirstStart().isContinueAfterError(), config.getClass().getSimpleName());
        }
    }

    @Test
    void contactDelete_modeAndFirstStart_areHardcoded() {
        var config = new ContactDeleteSchedulerPropertiesConfig();

        config.setMode(ONLY_FIRST);
        config.setFirstStart(new FirstStart(List.of(1), false));

        assertEquals(NORMAL, config.getMode());
        assertNull(config.getFirstStart());
    }

    @Test
    void baseSetters_propagateToGetters() {
        for (var config : configs) {
            config.setEnabled(true);
            config.setCronExpression("0 0 * * * *");
            config.setMode(ONLY_FIRST);
            config.setFirstStart(new FirstStart(List.of(1), false));

            assertTrue(config.isEnabled(), config.getClass().getSimpleName());
            assertEquals("0 0 * * * *", config.getCronExpression(), config.getClass().getSimpleName());
            if (config instanceof ContactDeleteSchedulerPropertiesConfig) {
                continue;
            }
            assertEquals(ONLY_FIRST, config.getMode(), config.getClass().getSimpleName());
            assertEquals(List.of(1), config.getFirstStart().getDelays(), config.getClass().getSimpleName());
            assertFalse(config.getFirstStart().isContinueAfterError(), config.getClass().getSimpleName());
        }
    }

    @Test
    void activityLog_specificFields() {
        var config = new ActivityLogSchedulerPropertiesConfig();
        assertEquals(100, config.getLimit());
        assertEquals(3, config.getMaxAgeYears());
        assertFalse(config.isSkipBatchError());
        assertNull(config.getForbiddenDescriptionPatterns());

        config.setLimit(7);
        config.setMaxAgeYears(1);
        config.setSkipBatchError(true);
        config.setForbiddenDescriptionPatterns(List.of("a", "b"));
        assertEquals(7, config.getLimit());
        assertEquals(1, config.getMaxAgeYears());
        assertTrue(config.isSkipBatchError());
        assertEquals(List.of("a", "b"), config.getForbiddenDescriptionPatterns());
    }

    @Test
    void credit_specificFields() {
        var config = new CreditSchedulerPropertiesConfig();
        assertEquals(25, config.getClientLimit());
        assertEquals(3, config.getMaxAgeYears());
        assertEquals(1000, config.getMaxClientCreditPerTask());
        assertFalse(config.isSkipBatchError());

        config.setClientLimit(10);
        config.setMaxAgeYears(2);
        config.setMaxClientCreditPerTask(500);
        config.setSkipBatchError(true);
        assertEquals(10, config.getClientLimit());
        assertEquals(2, config.getMaxAgeYears());
        assertEquals(500, config.getMaxClientCreditPerTask());
        assertTrue(config.isSkipBatchError());
    }

    @Test
    void email_specificFields() {
        var config = new EmailSchedulerPropertiesConfig();
        assertEquals(15, config.getClientLimit());
        assertEquals(3, config.getMaxAgeYears());
        assertEquals(1000, config.getMaxClientEmailsPerTask());
        assertEquals(25, config.getLimit());
        assertFalse(config.isSkipBatchError());
        assertEquals(List.of(), config.getMaskBodyMessagePatterns());

        config.setClientLimit(5);
        config.setMaxAgeYears(1);
        config.setMaxClientEmailsPerTask(100);
        config.setLimit(10);
        config.setSkipBatchError(true);
        config.setMaskBodyMessagePatterns(List.of("pattern"));
        assertEquals(5, config.getClientLimit());
        assertEquals(1, config.getMaxAgeYears());
        assertEquals(100, config.getMaxClientEmailsPerTask());
        assertEquals(10, config.getLimit());
        assertTrue(config.isSkipBatchError());
        assertEquals(List.of("pattern"), config.getMaskBodyMessagePatterns());
    }

    @Test
    void clientDetail_specificFields() {
        var config = new ClientDetailSchedulerPropertiesConfig();
        assertEquals(25, config.getClientLimit());
        assertFalse(config.isSkipBatchError());
        assertNull(config.getConcurrency());

        config.setClientLimit(20);
        config.setSkipBatchError(true);
        config.setConcurrency(4);
        assertEquals(20, config.getClientLimit());
        assertTrue(config.isSkipBatchError());
        assertEquals(4, config.getConcurrency());
    }

    @Test
    void contactDelete_specificFields() {
        var config = new ContactDeleteSchedulerPropertiesConfig();
        assertEquals(25, config.getLimit());

        config.setLimit(30);
        assertEquals(30, config.getLimit());
    }

    @Test
    void simpleConfigs_limitIs25_skipBatchErrorFalse() {
        assertFalse(new AffiliateSchedulerPropertiesConfig().isSkipBatchError());
        assertFalse(new ClientProductSchedulerPropertiesConfig().isSkipBatchError());
        assertFalse(new ClientSchedulerPropertiesConfig().isSkipBatchError());
        assertFalse(new ContactSchedulerPropertiesConfig().isSkipBatchError());
        assertFalse(new InvoiceSchedulerPropertiesConfig().isSkipBatchError());
        assertFalse(new OrderSchedulerPropertiesConfig().isSkipBatchError());
        assertFalse(new UserSchedulerPropertiesConfig().isSkipBatchError());

        assertEquals(25, new AffiliateSchedulerPropertiesConfig().getLimit());
        assertEquals(25, new ClientProductSchedulerPropertiesConfig().getLimit());
        assertEquals(25, new ClientSchedulerPropertiesConfig().getLimit());
        assertEquals(25, new ContactSchedulerPropertiesConfig().getLimit());
        assertEquals(25, new InvoiceSchedulerPropertiesConfig().getLimit());
        assertEquals(25, new OrderSchedulerPropertiesConfig().getLimit());
        assertEquals(25, new UserSchedulerPropertiesConfig().getLimit());
        assertEquals(3, new InvoiceSchedulerPropertiesConfig().getMaxAgeYears());
        assertEquals(3, new OrderSchedulerPropertiesConfig().getMaxAgeYears());

        var user = new UserSchedulerPropertiesConfig();
        user.setLimit(11);
        user.setSkipBatchError(true);
        assertEquals(11, user.getLimit());
        assertTrue(user.isSkipBatchError());
    }

    @Test
    void emptyConfigs_haveNoOwnFields() {
        assertEquals(NORMAL, new ClientGroupSchedulerPropertiesConfig().getMode());
        assertEquals(NORMAL, new PaymentMethodSchedulerPropertiesConfig().getMode());
        assertEquals(NORMAL, new ProductSchedulerPropertiesConfig().getMode());
    }

    @Test
    void modeEnum_hasThreeValues() {
        assertEquals(3, Mode.values().length);
        assertEquals(List.of("NORMAL", "FORCE_FIRST", "ONLY_FIRST"),
                List.of(Mode.NORMAL.name(), Mode.FORCE_FIRST.name(), Mode.ONLY_FIRST.name()));
    }
}
