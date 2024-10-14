package xyz.stasiak.brewservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrewFinishScheduler {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Value("${kafka.topic.brew-finish-requested}")
    private String brewFinishRequestedTopic;

    public void scheduleFinish(UUID brewId, int timeOfBrewing) {
        scheduler.schedule(() -> {
            try {
                log.info("Sending brew finish for brewId: {}", brewId);
                BrewFinishRequest brewFinishRequest = new BrewFinishRequest(brewId);
                kafkaTemplate.send(brewFinishRequestedTopic, objectMapper.writeValueAsString(brewFinishRequest));
            } catch (JsonProcessingException e) {
                log.error("Error while scheduling brew finish", e);
            }
        }, timeOfBrewing, TimeUnit.MILLISECONDS);
    }
}
