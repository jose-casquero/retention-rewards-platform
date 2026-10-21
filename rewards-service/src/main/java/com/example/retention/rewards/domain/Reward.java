package com.example.retention.rewards.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;


@Entity
@Table(name = "rewards")
public class Reward {
    @Id
    private UUID id;
    private String code;
    private String description;
    private int pointsCost;
    private Instant expiresAt;

    protected Reward() {
    }

    public Reward(UUID id, String code, String description, int pointsCost, Instant expiresAt) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.pointsCost = pointsCost;
        this.expiresAt = expiresAt;
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public int getPointsCost() { return pointsCost; }
    public Instant getExpiresAt() { return expiresAt; }
}
