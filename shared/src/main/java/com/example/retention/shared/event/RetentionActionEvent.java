package com.example.retention.shared.event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;


public record RetentionActionEvent(
        UUID actionId,
        UUID playerId,
        ActionType actionType,
        String reason,
        String preferredChannel,
        Map<String, String> attributes,
        Instant createdAt,
        String correlationId
) {
    public enum ActionType {
        WELCOME,
        REENGAGEMENT,
        LOYALTY_MILESTONE,
        REWARD_NOTIFICATION
    }
}
