package xyz.stasiak.waterservice;

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
    public void handleWaterRequested(String message) throws JsonProcessingException {
        try {
            WaterPrepareRequest waterPrepareRequest = objectMapper.readValue(message, WaterPrepareRequest.class);
            waterService.prepareWater(waterPrepareRequest);
            WaterPrepareResponse waterPrepareResponse = new WaterPrepareResponse(
                    waterPrepareRequest.brewId(), waterPrepareRequest.volume(), waterPrepareRequest.temperature()
            );
            kafkaTemplate.send(waterPreparedTopic, objectMapper.writeValueAsString(waterPrepareResponse));
        } catch (WaterException e) {
            log.error("Error while preparing water", e);
            WaterErrorMessage waterErrorMessage = new WaterErrorMessage(e.getBrewId(), e.getMessage());
            kafkaTemplate.send(waterErrorTopic, objectMapper.writeValueAsString(waterErrorMessage));
        }
    }

    @KafkaListener(topics = "${kafka.topic.water-cancel-requested}")
    public void handleWaterCancelRequested(String message) throws JsonProcessingException {
        WaterCancelRequest waterCancelRequest = objectMapper.readValue(message, WaterCancelRequest.class);
        waterService.cancelWater(waterCancelRequest);
        WaterCancelResponse waterCancelResponse = new WaterCancelResponse(
                waterCancelRequest.brewId()
        );
        kafkaTemplate.send(waterCancelledTopic, objectMapper.writeValueAsString(waterCancelResponse));
    }
}
