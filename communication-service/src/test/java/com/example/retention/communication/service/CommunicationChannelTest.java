package com.example.retention.communication.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;


class CommunicationChannelTest {
    @Test
    void shouldCreateConsumerWithCollaborators() {
        CommunicationConsumer consumer = new CommunicationConsumer(
                new com.fasterxml.jackson.databind.ObjectMapper(),
                Mockito.mock(com.example.retention.communication.domain.DeliveryRepository.class),
                Mockito.mock(com.example.retention.communication.domain.ProcessedActionRepository.class)
        );
        assertThat(consumer).isNotNull();
    }
}
