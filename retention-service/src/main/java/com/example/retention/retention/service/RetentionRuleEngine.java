package com.example.retention.retention.service;

import com.example.retention.shared.event.PlayerActivityEvent;
import com.example.retention.shared.event.RetentionActionEvent;
import com.example.retention.shared.event.RewardEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;


@Component
public class RetentionRuleEngine {
    public RetentionActionEvent evaluate(PlayerActivityEvent event) {
        return switch (event.activityType()) {
            case REGISTRATION -> action(event, RetentionActionEvent.ActionType.WELCOME,
                    "New player registration", "PUSH");
            case DEPOSIT -> action(event, RetentionActionEvent.ActionType.REENGAGEMENT,
                    "Deposit completed", "PUSH");
            case GAMEPLAY -> null;
            case SESSION_STARTED -> null;
            case SESSION_ENDED -> null;
            case CAMPAIGN_INTERACTION -> null;
        };
    }

    public RetentionActionEvent evaluateReward(RewardEvent event) {
        if (event.type() == RewardEvent.RewardEventType.TIER_CHANGED) {
            return action(event.playerId(), RetentionActionEvent.ActionType.LOYALTY_MILESTONE,
                    "Player reached loyalty tier " + event.rewardCode(), "PUSH", event.correlationId());
        }
        if (event.type() == RewardEvent.RewardEventType.REWARD_CLAIMED) {
            return action(event.playerId(), RetentionActionEvent.ActionType.REWARD_NOTIFICATION,
                    "Reward claimed: " + event.rewardCode(), "EMAIL", event.correlationId());
        }
        return null;
    }

    private RetentionActionEvent action(PlayerActivityEvent event, RetentionActionEvent.ActionType type,
                                        String reason, String channel) {
        return action(event.playerId(), type, reason, channel, event.correlationId());
    }

    private RetentionActionEvent action(UUID playerId, RetentionActionEvent.ActionType type,
                                        String reason, String channel, String correlationId) {
        return new RetentionActionEvent(UUID.randomUUID(), playerId, type, reason, channel,
                Map.of("ruleVersion", "1.0"), Instant.now(), correlationId);
    }
}
