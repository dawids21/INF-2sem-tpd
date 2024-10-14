package xyz.stasiak.waterservice;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${kafka.topic.water-requested}")
    private String waterRequestedTopic;

    @Value("${kafka.topic.water-prepared}")
    private String waterPreparedTopic;

    @Value("${kafka.topic.water-cancel-requested}")
    private String waterCancelRequestedTopic;

    @Value("${kafka.topic.water-cancelled}")
    private String waterCancelledTopic;

    @Value("${kafka.topic.water-error}")
    private String waterErrorTopic;

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

    @Bean
    public NewTopic waterErrorTopic() {
        return new NewTopic(waterErrorTopic, 1, (short) 1);
    }
}
