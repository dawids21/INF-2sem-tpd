package xyz.stasiak.brewservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrewKafkaService {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final BrewService brewService;
    private final BrewFinishScheduler scheduler;

    @Value("${kafka.topic.brew-started}")
    private String brewStartedTopic;

    @Value("${kafka.topic.brew-finished}")
    private String brewFinishedTopic;

    @Value("${kafka.topic.brew-cancelled}")
    private String brewCancelledTopic;

    @Value("${kafka.topic.brew-error}")
    private String brewErrorTopic;

    @KafkaListener(topics = "${kafka.topic.brew-requested}")
    public void handleBrewRequested(String message) throws JsonProcessingException, InterruptedException {
        log.info("Received message: {}", message);
        try {
            BrewStartRequest brewStartRequest = objectMapper.readValue(message, BrewStartRequest.class);
            BrewStartResponse brewStartResponse;
            try {
                brewStartResponse = brewService.startBrew(brewStartRequest);
            } catch (DataIntegrityViolationException e) {
                // retry in case of concurrent request
                log.info("Retrying starting brew request");
                brewStartResponse = brewService.startBrew(brewStartRequest);
            }
            scheduler.scheduleFinish(brewStartRequest.brewId(), brewStartResponse.timeOfBrewing());
            log.info("Sending start response: {}", brewStartResponse);
            kafkaTemplate.send(brewStartedTopic, objectMapper.writeValueAsString(brewStartResponse));
        } catch (BrewException e) {
            log.error("Error while starting brew", e);
            BrewErrorMessage brewErrorMessage = new BrewErrorMessage(e.getBrewId(), e.getMessage());
            kafkaTemplate.send(brewErrorTopic, objectMapper.writeValueAsString(brewErrorMessage));
        }
    }

    @KafkaListener(topics = "${kafka.topic.brew-finish-requested}")
    public void handleBrewFinishRequested(String message) throws JsonProcessingException {
        log.info("Received message: {}", message);
        try {
            BrewFinishRequest brewFinishRequest = objectMapper.readValue(message, BrewFinishRequest.class);
            BrewFinishResponse brewFinishResponse = brewService.finishBrew(brewFinishRequest);
            log.info("Sending finish response: {}", brewFinishResponse);
            kafkaTemplate.send(brewFinishedTopic, objectMapper.writeValueAsString(brewFinishResponse));
        } catch (BrewException e) {
            log.error("Error while finishing brew", e);
            BrewErrorMessage brewErrorMessage = new BrewErrorMessage(e.getBrewId(), e.getMessage());
            kafkaTemplate.send(brewErrorTopic, objectMapper.writeValueAsString(brewErrorMessage));
        }
    }

    @KafkaListener(topics = "${kafka.topic.brew-cancel-requested}")
    public void handleBrewCancelRequested(String message) throws JsonProcessingException {
        log.info("Received message: {}", message);
        BrewCancelRequest brewCancelRequest = objectMapper.readValue(message, BrewCancelRequest.class);
        BrewCancelResponse brewCancelResponse;
        try {
            brewCancelResponse = brewService.cancelBrew(brewCancelRequest);
        } catch (DataIntegrityViolationException e) {
            // retry in case of concurrent request
            log.info("Retrying cancel brew request");
            brewCancelResponse = brewService.cancelBrew(brewCancelRequest);
        }
        log.info("Sending cancel response: {}", brewCancelResponse);
        kafkaTemplate.send(brewCancelledTopic, objectMapper.writeValueAsString(brewCancelResponse));
    }
}
