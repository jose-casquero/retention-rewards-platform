package com.example.retention.retention.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;


@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    @Id
    private UUID id;
    private String topic;
    @Lob
    private String payload;
    private Instant createdAt;
    private boolean published;

    protected OutboxEvent() {
    }

    public OutboxEvent(UUID id, String topic, String payload, Instant createdAt) {
        this.id = id;
        this.topic = topic;
        this.payload = payload;
        this.createdAt = createdAt;
        this.published = false;
    }

    public UUID getId() { return id; }
    public String getTopic() { return topic; }
    public String getPayload() { return payload; }
    public boolean isPublished() { return published; }

    public void markPublished() {
        this.published = true;
    }
}
