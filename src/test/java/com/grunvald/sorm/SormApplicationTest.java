package com.grunvald.sorm;

import com.grunvald.sorm.config.NotificationPropertiesConfig;
import com.grunvald.sorm.service.system.notification.SystemNotificationService;
import com.grunvald.sorm.service.system.notification.model.SystemNotification;
import com.grunvald.sorm.service.system.notification.model.Type;
import com.grunvald.sorm.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.info.BuildProperties;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SormApplicationTest {

    @Mock
    private SystemNotificationService notificationService;
    @Mock
    private DateUtils date;
    @Mock
    private NotificationPropertiesConfig notificationConfig;

    @Test
    void startupRunner_reloadEnabled_pushesRebootNotification() throws Exception {
        var fixedNow = OffsetDateTime.parse("2026-01-01T00:00:00Z");
        when(notificationConfig.isReload()).thenReturn(true);
        when(date.now()).thenReturn(fixedNow);
        when(notificationService.push(any())).thenReturn(Mono.empty());

        app().startupRunner().run();

        ArgumentCaptor<SystemNotification> captor = ArgumentCaptor.forClass(SystemNotification.class);
        verify(notificationService).push(captor.capture());
        var notification = captor.getValue();
        assertEquals("sorm-935/1.2.3 has been rebooted", notification.getMessage());
        assertEquals(Type.RESTARTDB, notification.getType());
        assertEquals(fixedNow, notification.getCreatedAt());
    }

    @Test
    void startupRunner_reloadDisabled_doesNotPush() throws Exception {
        when(notificationConfig.isReload()).thenReturn(false);

        app().startupRunner().run();

        verifyNoInteractions(notificationService);
    }

    @Test
    void startupRunner_pushError_isSwallowed() {
        when(notificationConfig.isReload()).thenReturn(true);
        when(date.now()).thenReturn(OffsetDateTime.parse("2026-01-01T00:00:00Z"));
        when(notificationService.push(any())).thenReturn(Mono.error(new RuntimeException("boom")));

        assertDoesNotThrow(() -> app().startupRunner().run());
    }

    private SormApplication app() {
        var props = new Properties();
        props.setProperty("name", "sorm-935");
        props.setProperty("version", "1.2.3");
        return new SormApplication(notificationService, new BuildProperties(props), date, notificationConfig);
    }
}
