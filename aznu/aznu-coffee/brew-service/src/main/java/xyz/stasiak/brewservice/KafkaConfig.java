package xyz.stasiak.brewservice;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${kafka.topic.brew-requested}")
    private String brewRequestedTopic;

    @Value("${kafka.topic.brew-started}")
    private String brewStartedTopic;

    @Value("${kafka.topic.brew-finish-requested}")
    private String brewFinishRequestedTopic;

    @Value("${kafka.topic.brew-finished}")
    private String brewFinishedTopic;

    @Value("${kafka.topic.brew-cancel-requested}")
    private String brewCancelRequestedTopic;

    @Value("${kafka.topic.brew-cancelled}")
    private String brewCancelledTopic;

    @Value("${kafka.topic.brew-error}")
    private String brewErrorTopic;

    @Bean
    public NewTopic brewRequestedTopic() {
        return new NewTopic(brewRequestedTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic brewStartedTopic() {
        return new NewTopic(brewStartedTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic brewFinishRequestedTopic() {
        return new NewTopic(brewFinishRequestedTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic brewFinishedTopic() {
        return new NewTopic(brewFinishedTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic brewCancelRequestedTopic() {
        return new NewTopic(brewCancelRequestedTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic brewCancelledTopic() {
        return new NewTopic(brewCancelledTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic brewErrorTopic() {
        return new NewTopic(brewErrorTopic, 1, (short) 1);
    }
}
