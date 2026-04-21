package com.github.gabrielvavelar.binance_ingestion.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic cryptoTradesTopic() {
        return TopicBuilder.name("crypto-trades")
                .partitions(2)
                .replicas(1)
                .build();
    }
}
