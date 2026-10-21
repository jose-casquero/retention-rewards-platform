package com.example.retention.shared.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


public record PlayerActivityEvent(
        UUID eventId,
        UUID playerId,
        ActivityType activityType,
        BigDecimal amount,
        String currency,
        Instant occurredAt,
        String correlationId
) {
    public enum ActivityType {
        REGISTRATION,
        DEPOSIT,
        GAMEPLAY,
        SESSION_STARTED,
        SESSION_ENDED,
        CAMPAIGN_INTERACTION
    }
}
