package com.example.retention.rewards.api;

import com.example.retention.rewards.domain.LoyaltyAccount;
import com.example.retention.rewards.domain.LoyaltyAccountRepository;
import com.example.retention.rewards.domain.Reward;
import com.example.retention.rewards.domain.RewardRepository;
import com.example.retention.shared.event.EventTopics;
import com.example.retention.shared.event.RewardEvent;
import com.example.retention.shared.event.PlayerActivityEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.kafka.annotation.KafkaListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rewards")
@Tag(name = "Rewards")
public class RewardsController {
    private final LoyaltyAccountRepository accountRepository;
    private final RewardRepository rewardRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public RewardsController(LoyaltyAccountRepository accountRepository,
                             RewardRepository rewardRepository,
                             KafkaTemplate<String, String> kafkaTemplate,
                             ObjectMapper objectMapper) {
        this.accountRepository = accountRepository;
        this.rewardRepository = rewardRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/players/{playerId}/account")
    @Operation(summary = "Get loyalty account")
    public LoyaltyResponse account(@PathVariable UUID playerId) {
        LoyaltyAccount account = accountRepository.findById(playerId)
                .orElseGet(() -> new LoyaltyAccount(playerId));
        return new LoyaltyResponse(playerId, account.getPoints(), account.getTier());
    }

    @PostMapping("/catalog")
    @Operation(summary = "Create reward")
    public Reward create(@Valid @RequestBody CreateRewardRequest request) {
        Reward reward = new Reward(UUID.randomUUID(), request.code(), request.description(), request.pointsCost(), request.expiresAt());
        return rewardRepository.save(reward);
    }

    @GetMapping("/catalog")
    @Operation(summary = "List rewards")
    public List<Reward> list() {
        return rewardRepository.findAll();
    }

    @PostMapping("/players/{playerId}/claim")
    @Transactional
    @Operation(summary = "Claim a reward")
    public LoyaltyResponse claim(@PathVariable UUID playerId, @Valid @RequestBody ClaimRequest request) {
        Reward reward = rewardRepository.findByCode(request.rewardCode())
                .orElseThrow(() -> new IllegalArgumentException("Reward does not exist"));
        LoyaltyAccount account = accountRepository.findById(playerId)
                .orElseGet(() -> accountRepository.save(new LoyaltyAccount(playerId)));

        if (account.getPoints() < reward.getPointsCost()) {
            throw new IllegalStateException("Insufficient points");
        }

        account.addPoints(-reward.getPointsCost());
        accountRepository.save(account);
        publishRewardEvent(new RewardEvent(UUID.randomUUID(), playerId, RewardEvent.RewardEventType.REWARD_CLAIMED,
                -reward.getPointsCost(), reward.getCode(), Instant.now(), UUID.randomUUID().toString()));
        return new LoyaltyResponse(playerId, account.getPoints(), account.getTier());
    }

    @KafkaListener(topics = EventTopics.PLAYER_ACTIVITY, groupId = "rewards-service")
    @Transactional
    public void onPlayerActivity(String payload) {
        final PlayerActivityEvent event;
        try {
            event = objectMapper.readValue(payload, PlayerActivityEvent.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid player activity event", exception);
        }
        int points = switch (event.activityType()) {
            case REGISTRATION -> 25;
            case DEPOSIT -> 10;
            case GAMEPLAY -> 5;
            case SESSION_STARTED -> 2;
            case SESSION_ENDED -> 1;
            case CAMPAIGN_INTERACTION -> 15;
        };

        LoyaltyAccount account = accountRepository.findById(event.playerId())
                .orElseGet(() -> new LoyaltyAccount(event.playerId()));
        LoyaltyAccount.TierChange tierChange = account.addPoints(points);
        accountRepository.save(account);

        publishRewardEvent(new RewardEvent(UUID.randomUUID(), event.playerId(), RewardEvent.RewardEventType.POINTS_AWARDED,
                points, null, Instant.now(), event.correlationId()));

        if (!tierChange.oldTier().equals(tierChange.newTier())) {
            publishRewardEvent(new RewardEvent(UUID.randomUUID(), event.playerId(), RewardEvent.RewardEventType.TIER_CHANGED,
                    0, tierChange.newTier(), Instant.now(), event.correlationId()));
        }
    }

    private void publishRewardEvent(RewardEvent event) {
        try {
            kafkaTemplate.send(EventTopics.REWARD_EVENTS, event.playerId().toString(), objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize reward event", exception);
        }
    }

    public record CreateRewardRequest(@NotBlank String code, @NotBlank String description,
                                      @Min(1) int pointsCost, Instant expiresAt) {}

    public record ClaimRequest(@NotBlank String rewardCode) {}

    public record LoyaltyResponse(UUID playerId, int points, String tier) {}
}
