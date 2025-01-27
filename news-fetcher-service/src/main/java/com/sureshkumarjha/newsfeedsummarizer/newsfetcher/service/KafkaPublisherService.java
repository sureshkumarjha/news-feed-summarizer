package com.sureshkumarjha.newsfeedsummarizer.newsfetcher.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaPublisherService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.name}")
    private String kafkaTopic;

    public KafkaPublisherService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String documentKey) {
        kafkaTemplate.send(kafkaTopic, documentKey);
    }

    public void publish(String key, String documentKey) {
        kafkaTemplate.send(kafkaTopic, key, documentKey);
    }
}
