package com.example.retention.retention.service;

import com.example.retention.retention.domain.OutboxEvent;
import com.example.retention.retention.domain.OutboxEventRepository;
import com.example.retention.retention.domain.ProcessedEvent;
import com.example.retention.retention.domain.ProcessedEventRepository;
import com.example.retention.shared.event.EventTopics;
import com.example.retention.shared.event.PlayerActivityEvent;
import com.example.retention.shared.event.RewardEvent;
import com.example.retention.shared.event.RetentionActionEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
public class RetentionEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(RetentionEventConsumer.class);

    private final ProcessedEventRepository processedEventRepository;
    private final OutboxEventRepository outboxRepository;
    private final RetentionRuleEngine ruleEngine;
    private final ObjectMapper objectMapper;

    public RetentionEventConsumer(ProcessedEventRepository processedEventRepository,
                                  OutboxEventRepository outboxRepository,
                                  RetentionRuleEngine ruleEngine,
                                  ObjectMapper objectMapper) {
        this.processedEventRepository = processedEventRepository;
        this.outboxRepository = outboxRepository;
        this.ruleEngine = ruleEngine;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = EventTopics.PLAYER_ACTIVITY, groupId = "retention-service")
    @Transactional
    public void onPlayerActivity(String payload) {
        final PlayerActivityEvent event;
        try {
            event = objectMapper.readValue(payload, PlayerActivityEvent.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid player activity event", exception);
        }
        if (processedEventRepository.existsById(event.eventId())) {
            log.info("Duplicate player activity ignored: eventId={}", event.eventId());
            return;
        }
        processedEventRepository.save(new ProcessedEvent(event.eventId(), Instant.now()));
        storeAction(ruleEngine.evaluate(event));
    }

    @KafkaListener(topics = EventTopics.REWARD_EVENTS, groupId = "retention-reward-consumer")
    @Transactional
    public void onRewardEvent(String payload) {
        final RewardEvent event;
        try {
            event = objectMapper.readValue(payload, RewardEvent.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid reward event", exception);
        }
        if (processedEventRepository.existsById(event.eventId())) {
            log.info("Duplicate reward event ignored: eventId={}", event.eventId());
            return;
        }
        processedEventRepository.save(new ProcessedEvent(event.eventId(), Instant.now()));
        storeAction(ruleEngine.evaluateReward(event));
    }

    private void storeAction(RetentionActionEvent action) {
        if (action == null) {
            return;
        }
        try {
            String payload = objectMapper.writeValueAsString(action);
            outboxRepository.save(new OutboxEvent(action.actionId(), EventTopics.RETENTION_ACTIONS,
                    payload, action.createdAt()));
            log.info("Retention action created: actionId={}, playerId={}, actionType={}",
                    action.actionId(), action.playerId(), action.actionType());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize retention action", exception);
        }
    }
}
