package xyz.stasiak.gateway;

import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.stasiak.gateway.models.beans.BeansPrepareRequest;
import xyz.stasiak.gateway.models.water.WaterPrepareRequest;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CoffeeService extends RouteBuilder {

    private final CoffeeOrderRepository coffeeOrderRepository;

    @Value("${kafka.topic.coffee-requested}")
    private String coffeeRequestedTopic;

    @Value("${kafka.topic.water-requested}")
    private String waterRequestedTopic;

    @Value("${kafka.topic.water-prepared}")
    private String waterPreparedTopic;

    @Value("${kafka.topic.water-cancel-requested}")
    private String waterCancelRequestedTopic;

    @Value("${kafka.topic.water-cancelled}")
    private String waterCancelledTopic;

    @Value("${kafka.topic.beans-requested}")
    private String beansRequestedTopic;

    @Value("${kafka.topic.beans-prepared}")
    private String beansPreparedTopic;

    @Value("${kafka.topic.beans-cancel-requested}")
    private String beansCancelRequestedTopic;

    @Value("${kafka.topic.beans-cancelled}")
    private String beansCancelledTopic;

    @Override
    public void configure() throws Exception {
        gateway();
        water();
        beans();
    }

    public void gateway() {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .enableCORS(true)
                .contextPath("/api")
                .host("localhost")
                .port(8080)
                // turn on swagger api-doc
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Coffee machine API")
                .apiProperty("api.version", "1.0.0");

        rest("/coffee").description("Coffee machine REST service")
                .consumes("application/json")
                .produces("application/json")
                .post("/make").description("Make coffee").type(CoffeeMakeRequest.class).outType(CoffeeMakeResponse.class)
                .param().name("body").type(RestParamType.body).description("Coffee parameters to make").endParam()
                .responseMessage().code(200).message("Coffee making started").endResponseMessage()
                .to("direct:add-coffee-order");

        from("direct:add-coffee-order")
                .routeId("make-coffee")
                .log("(${routeId}) Request received")
                .process(exchange -> {
                    CoffeeMakeRequest request = exchange.getMessage().getBody(CoffeeMakeRequest.class);
                    UUID orderId = UUID.randomUUID();
                    CoffeeOrder coffeeOrder = new CoffeeOrder(orderId, request.beansWeight(), request.beansName(), request.waterVolume(), request.waterTemperature());
                    coffeeOrderRepository.save(coffeeOrder);
                    exchange.getMessage().setHeader("orderId", orderId.toString());
                })
                .log("Order created with id: ${header.orderId}")
                .to("direct:send-kafka-coffee-request")
                .to("direct:make-coffee-response");

        from("direct:send-kafka-coffee-request")
                .routeId("send-kafka-coffee-request")
                .log("(${routeId}) Sending order to Kafka")
                .marshal().json()
                .to("kafka:" + coffeeRequestedTopic);

        from("direct:make-coffee-response")
                .routeId("make-coffee-response")
                .log("(${routeId}) Sending response")
                .process(exchange -> {
                    String orderId = exchange.getMessage().getHeader("orderId", String.class);
                    exchange.getMessage().setBody(new CoffeeMakeResponse(UUID.fromString(orderId)));
                });

        from("kafka:" + coffeeRequestedTopic)
                .routeId("coffee-requested")
                .log("(${routeId}) Coffee requested")
                .unmarshal().json(CoffeeMakeRequest.class)
                .multicast()
                .to("direct:" + waterRequestedTopic)
                .to("direct:" + beansRequestedTopic);
    }

    public void water() {
        from("direct:" + waterRequestedTopic)
                .routeId(waterRequestedTopic)
                .log("(${routeId}) Sending water request")
                .transacted()
                .process(exchange -> {
                    String orderId = exchange.getMessage().getHeader("orderId", String.class);
                    CoffeeMakeRequest request = exchange.getMessage().getBody(CoffeeMakeRequest.class);
                    WaterPrepareRequest waterRequest = new WaterPrepareRequest(UUID.fromString(orderId), request.waterVolume(), request.waterTemperature());
                    exchange.getMessage().setBody(waterRequest);
                })
                .marshal().json()
                .to("kafka:" + waterRequestedTopic)
                .process(exchange -> {
                    String orderId = exchange.getMessage().getHeader("orderId", String.class);
                    CoffeeOrder coffeeOrder = coffeeOrderRepository.findByOrderId(UUID.fromString(orderId));
                    coffeeOrder.setWaterStatus(CoffeeOrder.Status.IN_PROGRESS);
                    coffeeOrderRepository.save(coffeeOrder);
                });

    }

    public void beans() {
        from("direct:" + beansRequestedTopic)
                .routeId(beansRequestedTopic)
                .log("(${routeId}) Sending beans request")
                .transacted()
                .process(exchange -> {
                    String orderId = exchange.getMessage().getHeader("orderId", String.class);
                    CoffeeMakeRequest request = exchange.getMessage().getBody(CoffeeMakeRequest.class);
                    BeansPrepareRequest beansRequest = new BeansPrepareRequest(UUID.fromString(orderId), request.beansName(), request.beansWeight());
                    exchange.getMessage().setBody(beansRequest);
                })
                .marshal().json()
                .to("kafka:" + beansRequestedTopic)
                .process(exchange -> {
                    String orderId = exchange.getMessage().getHeader("orderId", String.class);
                    CoffeeOrder coffeeOrder = coffeeOrderRepository.findByOrderId(UUID.fromString(orderId));
                    coffeeOrder.setBeansStatus(CoffeeOrder.Status.IN_PROGRESS);
                    coffeeOrderRepository.save(coffeeOrder);
                });
    }
}
