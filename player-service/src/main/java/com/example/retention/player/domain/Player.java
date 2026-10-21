package com.example.retention.player.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;


@Entity
@Table(name = "players")
public class Player {
    @Id
    private UUID id;
    private String email;
    private String country;
    private String preferredChannel;
    private boolean marketingOptIn;
    private Instant createdAt;
    private Instant lastActivityAt;

    protected Player() {
    }

    public Player(UUID id, String email, String country, String preferredChannel,
                  boolean marketingOptIn, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.country = country;
        this.preferredChannel = preferredChannel;
        this.marketingOptIn = marketingOptIn;
        this.createdAt = createdAt;
        this.lastActivityAt = createdAt;
    }

    public void touch(Instant activityAt) {
        this.lastActivityAt = activityAt;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getCountry() { return country; }
    public String getPreferredChannel() { return preferredChannel; }
    public boolean isMarketingOptIn() { return marketingOptIn; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastActivityAt() { return lastActivityAt; }
}
