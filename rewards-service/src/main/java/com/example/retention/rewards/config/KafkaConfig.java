package com.example.retention.rewards.config;

import com.example.retention.shared.event.EventTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaConfig {
    @Bean
    NewTopic rewardEventsTopic() {
        return TopicBuilder.name(EventTopics.REWARD_EVENTS).partitions(3).replicas(1).build();
    }
}
