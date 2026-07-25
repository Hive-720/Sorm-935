package com.grunvald.sorm.syncdatascheduler.scheduler;

import com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask;
import com.grunvald.sorm.syncdatascheduler.task.SchedulerTaskRepository;
import com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerCoordinator;
import com.grunvald.sorm.syncdatascheduler.scheduler.util.SchedulerHelper;
import com.grunvald.sorm.util.DateUtils;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.NORMAL;
import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.ONLY_FIRST;
import static com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask.Status.IN_PROGRESS;


@Slf4j
public abstract class Scheduler<D, C extends SchedulerPropertiesConfig> {
    protected final C config;
    private final SchedulerHelper schedulerHelper;
    private final SchedulerTaskRepository schedulerTaskRepository;
    private final TransactionalOperator transactionalOperator;
    private final SchedulerCoordinator schedulerCoordinator;
    protected final DateUtils date;
    private final SchedulerType schedulerType;
    private final CronExpression cron;


    public Scheduler(SchedulerType schedulerType, C config, SchedulerHelper schedulerHelper,
                     SchedulerTaskRepository schedulerTaskRepository, DateUtils date, TransactionalOperator transactional,
                     SchedulerCoordinator schedulerCoordinator) {
        this.config = config;
        this.schedulerHelper = schedulerHelper;
        this.schedulerTaskRepository = schedulerTaskRepository;
        this.date = date;
        this.transactionalOperator = transactional;
        this.schedulerCoordinator = schedulerCoordinator;
        this.cron = CronExpression.parse(config.getCronExpression());
        this.schedulerType = schedulerType;
    }

    @PostConstruct
    public void start() {
        waitDependencies()
                .then((config.getFirstStart() != null ? runFirstTask() : Mono.just(true)))
                .defaultIfEmpty(true)
                .onErrorResume(e -> config.getFirstStart().isContinueAfterError() ? Mono.just(true) : Mono.error(e))
                .doOnSuccess(ignore -> {
                    log.info("Scheduler [{}]: first task finished. Signalling dependents.", schedulerType);
                    schedulerCoordinator.signalReady(schedulerType);
                })
                .flatMapMany(bool -> config.getMode() != ONLY_FIRST ? runTaskProcessByCron() : Mono.empty())
                .subscribe(r -> log.info("Scheduler [{}]: completed as planned.", schedulerType),
                        e -> schedulerHelper.handleFailure(e, schedulerType)
                );
    }

    private Mono<Void> waitDependencies() {
        var dependencies = getDependencies();
        if (dependencies == null || dependencies.isEmpty()) {
            return Mono.empty();
        }

        log.info("Scheduler [{}]: waiting for dependencies - {}", schedulerType, dependencies);

        return Flux.fromIterable(dependencies)
                .flatMap(schedulerCoordinator::waitUploadFor)
                .then()
                .doOnSuccess(v -> log.info("Scheduler [{}]: dependencies satisfied.", schedulerType));
    }

    private Mono<Boolean> runFirstTask() {
        log.info("Scheduler [{}]: first task starting.", schedulerType);
        return getTaskForFirstProcess()
                .flatMap(this::executeTask)
                .doOnError(e -> schedulerHelper.handleError(e, schedulerType))
                .retryWhen(schedulerHelper.createRetry(config.getFirstStart().getDelays(), schedulerType))
                .thenReturn(true);
    }

    private Flux<Boolean> runTaskProcessByCron() {
        return schedulerHelper.delayByCron(cron, schedulerType)
                .flatMap(tick -> getTaskForProcessByCron())
                .flatMap(this::executeTask)
                .doOnError(e -> schedulerHelper.handleError(e, schedulerType))
                .onErrorResume(e -> Mono.just(true))
                .repeat();
    }

    private Mono<SchedulerTask> getTaskForFirstProcess() {
        if (!saveStateTask()) {
            return Mono.just(SchedulerTask.EMPTY);
        }

        return schedulerTaskRepository.findBySchedulerType(schedulerType)
                .filter(task -> task.isNotFinished() || config.getMode() != NORMAL)
                .doOnNext(task -> {
                    if (config.getMode() != NORMAL) {
                        clearTask(task);
                    }
                })
                .switchIfEmpty(schedulerTaskRepository.add(new SchedulerTask(schedulerType, date.now())));
    }

    protected Mono<SchedulerTask> getTaskForProcessByCron() {
        if (!saveStateTask()) {
            return Mono.just(SchedulerTask.EMPTY);
        }

        return schedulerTaskRepository.findBySchedulerType(schedulerType)
                .doOnNext(this::clearSyncTaskIfFinished)
                .switchIfEmpty(schedulerTaskRepository.add(new SchedulerTask(schedulerType, date.now())));
    }

    private void clearSyncTaskIfFinished(SchedulerTask schedulerTask) {
        if (schedulerTask.isFinished()) {
            clearTask(schedulerTask);
        }
    }

    private void clearTask(SchedulerTask schedulerTask) {
        schedulerTask.setOffset(0);
        schedulerTask.setCursor(null);
        schedulerTask.setStatus(IN_PROGRESS);
    }

    private Mono<Boolean> executeTask(SchedulerTask schedulerTask) {
        return executeStep(schedulerTask.getOffset(), schedulerTask.getCursor())
                .expand(stepResult -> {
                    if (!stepResult.hasNextPage) {
                        return Mono.empty();
                    }

                    return executeStep(stepResult.nextOffset, stepResult.getNextCursor());
                })
                .concatMap(stepResult -> apply(stepResult, schedulerTask))
                .last();
    }

    protected abstract Mono<Step<D>> executeStep(Long offset, String cursor);

    protected abstract Mono<Boolean> afterStep(D data);

    private Mono<Boolean> apply(Step<D> step, SchedulerTask schedulerTask) {
        return transactionalOperator.transactional(
                afterStep(step.data)
                        .flatMap(bool -> saveStateTask() ? updateTask(schedulerTask, step) : Mono.empty())
                        .thenReturn(true)
        );
    }

    private Mono<SchedulerTask> updateTask(SchedulerTask schedulerTask, Step<D> step) {
        schedulerTask.setCursor(step.getNextCursor());
        schedulerTask.setOffset(step.nextOffset);
        if (!step.hasNextPage) {
            schedulerTask.finish(date.now());
        }
        return schedulerTaskRepository.update(schedulerTask);
    }

    protected boolean saveStateTask() {
        return true;
    }

    protected Set<SchedulerType> getDependencies() {
        return null;
    }

    @Getter
    @AllArgsConstructor
    protected static class Step<D> {
        private D data;
        private boolean hasNextPage;
        private Long nextOffset;
        private String nextCursor;
    }
}