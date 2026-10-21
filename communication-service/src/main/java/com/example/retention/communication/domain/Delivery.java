package com.example.retention.communication.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;


@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id
    private UUID id;
    private UUID playerId;
    private String reason;
    @Enumerated(EnumType.STRING)
    private Channel channel;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
    private Instant createdAt;

    protected Delivery() {
    }

    public Delivery(UUID id, UUID playerId, String reason, Channel channel) {
        this.id = id;
        this.playerId = playerId;
        this.reason = reason;
        this.channel = channel;
        this.status = DeliveryStatus.QUEUED;
        this.createdAt = Instant.now();
    }

    public void markSent() {
        this.status = DeliveryStatus.SENT;
    }

    public UUID getId() { return id; }
    public UUID getPlayerId() { return playerId; }
    public String getReason() { return reason; }
    public Channel getChannel() { return channel; }
    public DeliveryStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public enum Channel { PUSH, EMAIL, SMS }
    public enum DeliveryStatus { QUEUED, SENT, FAILED }
}
