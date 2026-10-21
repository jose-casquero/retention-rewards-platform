package com.example.retention.player.api;

import com.example.retention.player.domain.Player;
import com.example.retention.player.domain.PlayerRepository;
import com.example.retention.shared.event.EventTopics;
import com.example.retention.shared.event.PlayerActivityEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/players")
@Tag(name = "Players")
public class PlayerController {
    private static final Logger log = LoggerFactory.getLogger(PlayerController.class);

    private final PlayerRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PlayerController(PlayerRepository repository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @Operation(summary = "Create a player")
    public PlayerResponse create(@Valid @RequestBody CreatePlayerRequest request) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        Player player = new Player(id, request.email(), request.country(), request.preferredChannel(), request.marketingOptIn(), now);
        repository.save(player);

        publishActivity(player, PlayerActivityEvent.ActivityType.REGISTRATION, null);
        log.info("Player created: playerId={}", id);
        return toResponse(player);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a player")
    public ResponseEntity<PlayerResponse> get(@PathVariable UUID id) {
        return repository.findById(id).map(p -> ResponseEntity.ok(toResponse(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/activity")
    @Operation(summary = "Record player activity")
    public ResponseEntity<Void> activity(@PathVariable UUID id, @Valid @RequestBody ActivityRequest request) {
        Player player = repository.findById(id).orElse(null);
        if (player == null) {
            return ResponseEntity.notFound().build();
        }
        Instant occurredAt = request.occurredAt() == null ? Instant.now() : request.occurredAt();
        player.touch(occurredAt);
        repository.save(player);
        publishActivity(player, request.activityType(), request.amount(), occurredAt);
        log.info("Player activity recorded: playerId={}, activityType={}", id, request.activityType());
        return ResponseEntity.accepted().build();
    }

    private void publishActivity(Player player, PlayerActivityEvent.ActivityType type, BigDecimal amount) {
        publishActivity(player, type, amount, Instant.now());
    }

    private void publishActivity(Player player, PlayerActivityEvent.ActivityType type, BigDecimal amount, Instant occurredAt) {
        PlayerActivityEvent event = new PlayerActivityEvent(
                UUID.randomUUID(), player.getId(), type, amount, "EUR", occurredAt, UUID.randomUUID().toString());
        try {
            kafkaTemplate.send(EventTopics.PLAYER_ACTIVITY, player.getId().toString(), objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize player activity", exception);
        }
    }

    private PlayerResponse toResponse(Player player) {
        return new PlayerResponse(player.getId(), player.getEmail(), player.getCountry(),
                player.getPreferredChannel(), player.isMarketingOptIn(), player.getCreatedAt(), player.getLastActivityAt());
    }

    public record CreatePlayerRequest(
            @NotBlank @Email String email,
            @NotBlank String country,
            @NotBlank String preferredChannel,
            boolean marketingOptIn
    ) {}

    public record ActivityRequest(
            PlayerActivityEvent.ActivityType activityType,
            BigDecimal amount,
            Instant occurredAt
    ) {}

    public record PlayerResponse(
            UUID id, String email, String country, String preferredChannel,
            boolean marketingOptIn, Instant createdAt, Instant lastActivityAt
    ) {}
}
