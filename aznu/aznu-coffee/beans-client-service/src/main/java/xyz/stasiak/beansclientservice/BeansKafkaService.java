package xyz.stasiak.beansclientservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeansKafkaService {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final BeansService beansService;

    @Value("${kafka.topic.beans-prepared}")
    private String beansPreparedTopic;

    @Value("${kafka.topic.beans-cancelled}")
    private String beansCancelledTopic;

    @Value("${kafka.topic.beans-error}")
    private String beansErrorTopic;

    @KafkaListener(topics = "${kafka.topic.beans-requested}")
    public void handleBeansRequested(String message) throws JsonProcessingException {
        log.info("Received beans requested message: {}", message);
        try {
            BeansPrepareRequest beansPrepareRequest = objectMapper.readValue(message, BeansPrepareRequest.class);
            BeansPrepareResponse beansPrepareResponse = beansService.prepareBeans(beansPrepareRequest);
            log.info("Sending beans prepared message: {}", beansPrepareResponse);
            kafkaTemplate.send(beansPreparedTopic, objectMapper.writeValueAsString(beansPrepareResponse));
        } catch (BeansException e) {
            log.error("Error while preparing beans", e);
            BeansErrorMessage beansErrorMessage = new BeansErrorMessage(e.getBrewId(), e.getMessage());
            kafkaTemplate.send(beansErrorTopic, objectMapper.writeValueAsString(beansErrorMessage));
        }
    }

    @KafkaListener(topics = "${kafka.topic.beans-cancel-requested}")
    public void handleBeansCancelRequested(String message) throws JsonProcessingException {
        log.info("Received beans cancel requested message: {}", message);
        BeansCancelRequest beansCancelRequest = objectMapper.readValue(message, BeansCancelRequest.class);
        BeansCancelResponse beansCancelResponse;
        beansCancelResponse = beansService.cancelBeans(beansCancelRequest);
        log.info("Sending beans cancelled message: {}", beansCancelResponse);
        kafkaTemplate.send(beansCancelledTopic, objectMapper.writeValueAsString(beansCancelResponse));
    }
}
