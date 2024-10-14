package xyz.stasiak.waterservice;

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
public class WaterKafkaService {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final WaterService waterService;

    @Value("${kafka.topic.water-prepared}")
    private String waterPreparedTopic;

    @Value("${kafka.topic.water-cancelled}")
    private String waterCancelledTopic;

    @Value("${kafka.topic.water-error}")
    private String waterErrorTopic;

    @KafkaListener(topics = "${kafka.topic.water-requested}")
    public void handleWaterRequested(String message) throws JsonProcessingException, InterruptedException {
        log.info("Received message: {}", message);
        try {
            WaterPrepareRequest waterPrepareRequest = objectMapper.readValue(message, WaterPrepareRequest.class);
            WaterPrepareResponse waterPrepareResponse ;
            try {
                waterPrepareResponse = waterService.prepareWater(waterPrepareRequest);
            } catch (DataIntegrityViolationException e) {
                // retry in case of concurrent request
                log.info("Retrying prepare water request");
                waterPrepareResponse = waterService.prepareWater(waterPrepareRequest);
            }
            log.info("Sending prepare response: {}", waterPrepareResponse);
            kafkaTemplate.send(waterPreparedTopic, objectMapper.writeValueAsString(waterPrepareResponse));
        } catch (WaterException e) {
            log.error("Error while preparing water", e);
            WaterErrorMessage waterErrorMessage = new WaterErrorMessage(e.getBrewId(), e.getMessage());
            kafkaTemplate.send(waterErrorTopic, objectMapper.writeValueAsString(waterErrorMessage));
        }
    }

    @KafkaListener(topics = "${kafka.topic.water-cancel-requested}")
    public void handleWaterCancelRequested(String message) throws JsonProcessingException {
        log.info("Received message: {}", message);
        WaterCancelRequest waterCancelRequest = objectMapper.readValue(message, WaterCancelRequest.class);
        WaterCancelResponse waterCancelResponse;
        try {
            waterCancelResponse = waterService.cancelWater(waterCancelRequest);
        } catch (DataIntegrityViolationException e) {
            // retry in case of concurrent request
            log.info("Retrying cancel water request");
            waterCancelResponse = waterService.cancelWater(waterCancelRequest);
        }
        log.info("Sending cancel response: {}", waterCancelResponse);
        kafkaTemplate.send(waterCancelledTopic, objectMapper.writeValueAsString(waterCancelResponse));
    }
}
