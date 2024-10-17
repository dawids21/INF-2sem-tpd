package xyz.stasiak.beansclientservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

@SpringBootTest
class BeansClientServiceApplicationTests {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${kafka.topic.beans-requested}")
    private String beansRequestedTopic;

    @Test
    void testBeansPrepare() throws JsonProcessingException, InterruptedException {
        UUID brewId = UUID.randomUUID();
        BeansPrepareRequest request = new BeansPrepareRequest(brewId, "Colombia");
        kafkaTemplate.send(beansRequestedTopic, objectMapper.writeValueAsString(request));
        BeansCancelRequest cancelRequest = new BeansCancelRequest(brewId);
        kafkaTemplate.send("coffee-beans-cancel-requested", objectMapper.writeValueAsString(cancelRequest));
        Thread.sleep(1000);
    }

}
