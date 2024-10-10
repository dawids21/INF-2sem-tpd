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

//    @Bean
//    public Map<String, Object> producerConfigs() {
//        Map<String, Object> props =
//                new HashMap<>(kafkaProperties.buildProducerProperties(null));
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//                StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//                StringSerializer.class);
//        return props;
//    }
//
//    @Bean
//    public ProducerFactory<String, String> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(producerConfigs());
//    }
//
//    @Bean
//    public ConsumerFactory<String, String> consumerFactory() {
//        Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildConsumerProperties(null));
//        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        return new DefaultKafkaConsumerFactory<>(configProps);
//    }
//
//    @Bean
//    public KafkaTemplate<String, String> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        return factory;
//    }

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
