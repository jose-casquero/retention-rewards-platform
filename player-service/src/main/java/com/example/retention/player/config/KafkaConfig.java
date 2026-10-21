package com.example.retention.player.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import com.example.retention.shared.event.EventTopics;


@Configuration
public class KafkaConfig {
    @Bean
    NewTopic playerActivityTopic() {
        return TopicBuilder.name(EventTopics.PLAYER_ACTIVITY).partitions(3).replicas(1).build();
    }
}
