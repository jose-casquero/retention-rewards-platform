package com.example.retention.rewards.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface RewardRepository extends JpaRepository<Reward, UUID> {
    Optional<Reward> findByCode(String code);
}
