package com.example.retention.rewards.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, UUID> {
}
