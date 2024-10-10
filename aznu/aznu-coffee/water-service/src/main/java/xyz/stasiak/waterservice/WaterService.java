package xyz.stasiak.waterservice;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WaterService {

    @KafkaListener(topics = "${kafka.topic.water-requested}")
    public void handleWaterRequested(String message) {
        System.out.println("Water requested: " + message);
    }

    @KafkaListener(topics = "${kafka.topic.water-prepared}")
    public void handleWaterPrepared(String message) {
        System.out.println("Water prepared: " + message);
    }

    @KafkaListener(topics = "${kafka.topic.water-cancel-requested}")
    public void handleWaterCancelRequested(String message) {
        System.out.println("Water cancel requested: " + message);
    }

    @KafkaListener(topics = "${kafka.topic.water-cancelled}")
    public void handleWaterCancelled(String message) {
        System.out.println("Water cancelled: " + message);
    }

}
