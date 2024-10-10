package xyz.stasiak.beansclientservice;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Value("${kafka.topic.beans-requested}")
    private String beansRequestedTopic;

    @Value("${kafka.topic.beans-prepared}")
    private String beansPreparedTopic;

    @Value("${kafka.topic.beans-cancel-requested}")
    private String beansCancelRequestedTopic;

    @Value("${kafka.topic.beans-cancelled}")
    private String beansCancelledTopic;

    @Bean
    public NewTopic beansRequestedTopic() {
        return new NewTopic(beansRequestedTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic beansPreparedTopic() {
        return new NewTopic(beansPreparedTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic beansCancelRequestedTopic() {
        return new NewTopic(beansCancelRequestedTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic beansCancelledTopic() {
        return new NewTopic(beansCancelledTopic, 1, (short) 1);
    }
}
