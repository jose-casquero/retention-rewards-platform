package com.example.retention.communication.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;


@Entity
@Table(name = "processed_actions")
public class ProcessedAction {
    @Id
    private UUID actionId;
    private Instant processedAt;

    protected ProcessedAction() {
    }

    public ProcessedAction(UUID actionId, Instant processedAt) {
        this.actionId = actionId;
        this.processedAt = processedAt;
    }
}
