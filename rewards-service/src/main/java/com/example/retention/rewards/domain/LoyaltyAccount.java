package com.example.retention.rewards.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;


@Entity
@Table(name = "loyalty_accounts")
public class LoyaltyAccount {
    @Id
    private UUID playerId;
    private int points;
    private String tier;

    protected LoyaltyAccount() {
    }

    public LoyaltyAccount(UUID playerId) {
        this.playerId = playerId;
        this.points = 0;
        this.tier = "BRONZE";
    }

    public TierChange addPoints(int additionalPoints) {
        this.points += additionalPoints;
        String oldTier = this.tier;
        this.tier = tierFor(points);
        return new TierChange(oldTier, this.tier);
    }

    private String tierFor(int value) {
        if (value >= 1000) return "PLATINUM";
        if (value >= 500) return "GOLD";
        if (value >= 200) return "SILVER";
        return "BRONZE";
    }

    public UUID getPlayerId() { return playerId; }
    public int getPoints() { return points; }
    public String getTier() { return tier; }

    public record TierChange(String oldTier, String newTier) {}
}
