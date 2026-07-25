package com.grunvald.sorm.syncdatascheduler.task.model;

import com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

import static com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask.Status.FINISHED;
import static com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask.Status.IN_PROGRESS;


@Getter
@Setter
public class SchedulerTask {

    public static final SchedulerTask EMPTY = new SchedulerTask(null, null);

    private Long id;
    private SchedulerType schedulerType;
    private String cursor;
    private long offset;
    private Status status;
    private OffsetDateTime startAt;
    private OffsetDateTime finishAt;


    public SchedulerTask(SchedulerType schedulerType, OffsetDateTime startAt) {
        this.schedulerType = schedulerType;
        this.status = IN_PROGRESS;
        this.startAt = startAt;
    }

    public void finish(OffsetDateTime offsetDateTime) {
        status = FINISHED;
        finishAt = offsetDateTime;
    }

    public boolean isNotFinished() {
        return !FINISHED.equals(status);
    }

    public boolean isFinished() {
        return FINISHED.equals(status);
    }

    public enum Status {
        IN_PROGRESS,
        FINISHED
    }
}
