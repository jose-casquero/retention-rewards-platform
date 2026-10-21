package com.example.retention.shared.event;

import java.time.Instant;
import java.util.UUID;


public record RewardEvent(
        UUID eventId,
        UUID playerId,
        RewardEventType type,
        int points,
        String rewardCode,
        Instant occurredAt,
        String correlationId
) {
    public enum RewardEventType {
        POINTS_AWARDED,
        TIER_CHANGED,
        REWARD_CLAIMED
    }
}
