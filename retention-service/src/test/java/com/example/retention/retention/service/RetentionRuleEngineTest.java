package com.example.retention.retention.service;

import com.example.retention.shared.event.PlayerActivityEvent;
import com.example.retention.shared.event.RetentionActionEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class RetentionRuleEngineTest {
    @Test
    void shouldCreateWelcomeActionForRegistration() {
        RetentionRuleEngine engine = new RetentionRuleEngine();
        PlayerActivityEvent event = new PlayerActivityEvent(UUID.randomUUID(), UUID.randomUUID(),
                PlayerActivityEvent.ActivityType.REGISTRATION, null, "EUR", Instant.now(), "corr-1");

        RetentionActionEvent action = engine.evaluate(event);

        assertThat(action).isNotNull();
        assertThat(action.actionType()).isEqualTo(RetentionActionEvent.ActionType.WELCOME);
        assertThat(action.preferredChannel()).isEqualTo("PUSH");
    }
}
