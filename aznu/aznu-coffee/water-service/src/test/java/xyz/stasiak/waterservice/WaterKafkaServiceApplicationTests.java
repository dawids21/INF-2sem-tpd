package xyz.stasiak.waterservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
class WaterKafkaServiceApplicationTests {

    @Container
    @ServiceConnection
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${kafka.topic.water-requested}")
    private String waterRequestedTopic;

    @Autowired
    private WaterOrderRepository waterOrderRepository;

    @Test
    void testWaterPrepare() throws JsonProcessingException {
        UUID brewId = UUID.randomUUID();
        WaterPrepareRequest request = new WaterPrepareRequest(brewId, 100, 90);
        kafkaTemplate.send(waterRequestedTopic, objectMapper.writeValueAsString(request));

        await().atMost(Durations.TEN_SECONDS).untilAsserted(() -> {
            Optional<WaterOrder> waterOrder = waterOrderRepository.findByBrewId(brewId);
            assertThat(waterOrder).isPresent();
            assertThat(waterOrder.get().getStatus()).isEqualTo(WaterOrder.Status.PREPARED);
            assertThat(waterOrder.get().getVolume()).isEqualTo(100);
            assertThat(waterOrder.get().getTemperature()).isEqualTo(90);
        });
    }

}
