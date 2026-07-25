package com.grunvald.sorm.syncdatascheduler.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.grunvald.sorm.syncdatascheduler.config.SchedulerPropertiesConfig.Mode.NORMAL;


@Getter
@Setter
@Validated
public abstract class SchedulerPropertiesConfig {
    private boolean enabled = false;
    private Mode mode = NORMAL;
    @NotBlank
    private String cronExpression;
    @Valid
    private FirstStart firstStart = FirstStart.DEFAULT;


    @Setter
    @Getter
    @AllArgsConstructor
    public static class FirstStart {
        private static FirstStart DEFAULT = new FirstStart(List.of(1000, 5000, 10000, 20000, 60000, 120000), true);

        private List<@Positive Integer> delays;
        private boolean continueAfterError;
    }

    public enum Mode {
        NORMAL,
        FORCE_FIRST,
        ONLY_FIRST
    }
}