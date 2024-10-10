package xyz.stasiak.waterservice;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Value("${kafka.topic.water-requested}")
    private String waterRequestedTopic;

    @Value("${kafka.topic.water-prepared}")
    private String waterPreparedTopic;

    @Value("${kafka.topic.water-cancel-requested}")
    private String waterCancelRequestedTopic;

    @Value("${kafka.topic.water-cancelled}")
    private String waterCancelledTopic;

    @Bean
    public NewTopic waterRequestedTopic() {
        return new NewTopic(waterRequestedTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic waterPreparedTopic() {
        return new NewTopic(waterPreparedTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic waterCancelRequestedTopic() {
        return new NewTopic(waterCancelRequestedTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic waterCancelledTopic() {
        return new NewTopic(waterCancelledTopic, 1, (short) 1);
    }
}
