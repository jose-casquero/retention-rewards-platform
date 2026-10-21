package com.example.retention.communication.service;

import com.example.retention.communication.domain.Delivery;
import com.example.retention.communication.domain.DeliveryRepository;
import com.example.retention.communication.domain.ProcessedAction;
import com.example.retention.communication.domain.ProcessedActionRepository;
import com.example.retention.shared.event.EventTopics;
import com.example.retention.shared.event.RetentionActionEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
public class CommunicationConsumer {
    private static final Logger log = LoggerFactory.getLogger(CommunicationConsumer.class);

    private final ObjectMapper objectMapper;
    private final DeliveryRepository deliveryRepository;
    private final ProcessedActionRepository processedActionRepository;

    public CommunicationConsumer(ObjectMapper objectMapper, DeliveryRepository deliveryRepository,
                                 ProcessedActionRepository processedActionRepository) {
        this.objectMapper = objectMapper;
        this.deliveryRepository = deliveryRepository;
        this.processedActionRepository = processedActionRepository;
    }

    @KafkaListener(topics = EventTopics.RETENTION_ACTIONS, groupId = "communication-service")
    @Transactional
    public void onRetentionAction(String payload) {
        try {
            RetentionActionEvent action = objectMapper.readValue(payload, RetentionActionEvent.class);
            if (processedActionRepository.existsById(action.actionId())) {
                log.info("Duplicate retention action ignored: actionId={}", action.actionId());
                return;
            }
            processedActionRepository.save(new ProcessedAction(action.actionId(), java.time.Instant.now()));
            Delivery.Channel channel = parseChannel(action.preferredChannel());
            Delivery delivery = new Delivery(UUID.randomUUID(), action.playerId(), action.reason(), channel);
            delivery.markSent();
            deliveryRepository.save(delivery);
            log.info("Communication delivered: deliveryId={}, playerId={}, channel={}, actionType={}",
                    delivery.getId(), delivery.getPlayerId(), delivery.getChannel(), action.actionType());
        } catch (Exception exception) {
            log.error("Failed to process retention action: error={}", exception.getMessage(), exception);
            throw new IllegalStateException("Communication processing failed", exception);
        }
    }

    private Delivery.Channel parseChannel(String value) {
        try {
            return Delivery.Channel.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return Delivery.Channel.EMAIL;
        }
    }
}
