package com.example.retention.rewards.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class LoyaltyAccountTest {
    @Test
    void shouldMovePlayerToGoldAtFiveHundredPoints() {
        LoyaltyAccount account = new LoyaltyAccount(UUID.randomUUID());
        account.addPoints(500);
        assertThat(account.getTier()).isEqualTo("GOLD");
        assertThat(account.getPoints()).isEqualTo(500);
    }
}
