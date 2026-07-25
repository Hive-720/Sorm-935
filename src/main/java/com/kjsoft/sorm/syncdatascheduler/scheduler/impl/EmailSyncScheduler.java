package com.kjsoft.sorm.syncdatascheduler.scheduler.impl;

import com.kjsoft.sorm.service.whmcsdata.datasyncservice.email.EmailSyncPage;
import com.kjsoft.sorm.service.whmcsdata.datasyncservice.email.EmailSyncService;
import com.kjsoft.sorm.syncdatascheduler.config.impl.EmailSchedulerPropertiesConfig;
import com.kjsoft.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.kjsoft.sorm.syncdatascheduler.scheduler.Scheduler;
import com.kjsoft.sorm.syncdatascheduler.scheduler.SchedulerType;
import com.kjsoft.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.kjsoft.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.kjsoft.sorm.util.DateUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.Set;

import static com.kjsoft.sorm.syncdatascheduler.scheduler.SchedulerType.CLIENT;
import static com.kjsoft.sorm.syncdatascheduler.scheduler.SchedulerType.EMAIL;


@Component
@ConditionalOnBean(EmailSchedulerPropertiesConfig.class)
public class EmailSyncScheduler extends Scheduler<EmailSyncPage, EmailSchedulerPropertiesConfig> {
    private final EmailSyncService emailSyncService;


    public EmailSyncScheduler(EmailSchedulerPropertiesConfig config, SchedulerHelper schedulerHelper,
                              SchedulerTaskRepository schedulerTaskRepository, DateUtils dateTime,
                              TransactionalOperator transactional, SchedulerCoordinator coordinator,
                              EmailSyncService emailSyncService) {
        super(EMAIL, config, schedulerHelper, schedulerTaskRepository, dateTime, transactional, coordinator);
        this.emailSyncService = emailSyncService;
    }

    @Override
    protected Mono<Step<EmailSyncPage>> executeStep(Long ignored, String cursor) {
        var historyDepthDate = date.now().minusYears(config.getMaxAgeYears());
        var cursorLong = cursor != null ? Long.parseLong(cursor) : null;
        return emailSyncService.getActualData(cursorLong, config.getLimit(), config.getMaxClientEmailsPerTask(),
                        historyDepthDate, config.getMaskBodyMessagePatterns()
                )
                .map(page -> new Step<>(
                                page,
                                page.isHasNextPage(),
                                null,
                                page.getCursor() != null ? String.valueOf(page.getCursor()) : null
                        )
                );
    }

    @Override
    protected Mono<Boolean> afterStep(EmailSyncPage page) {
        return emailSyncService.applyActualData(page);
    }

    @Override
    protected Set<SchedulerType> getDependencies() {
        return Set.of(CLIENT);
    }
}