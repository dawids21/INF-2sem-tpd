package xyz.stasiak.gateway;

import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import xyz.stasiak.gateway.models.beans.*;
import xyz.stasiak.gateway.models.brew.BrewFinishResponse;
import xyz.stasiak.gateway.models.brew.BrewStartRequest;
import xyz.stasiak.gateway.models.brew.BrewStartResponse;
import xyz.stasiak.gateway.models.water.WaterCancelRequest;
import xyz.stasiak.gateway.models.water.WaterErrorMessage;
import xyz.stasiak.gateway.models.water.WaterPrepareRequest;
import xyz.stasiak.gateway.models.water.WaterPrepareResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Value("${kafka.topic.water-error}")
    private String waterErrorTopic;

    @Value("${kafka.topic.beans-requested}")
    private String beansRequestedTopic;

    @Value("${kafka.topic.beans-prepared}")
    private String beansPreparedTopic;

    @Value("${kafka.topic.beans-cancel-requested}")
    private String beansCancelRequestedTopic;

    @Value("${kafka.topic.beans-cancelled}")
    private String beansCancelledTopic;

    @Value("${kafka.topic.beans-error}")
    private String beansErrorTopic;

    @Value("${kafka.topic.brew-requested}")
    private String brewRequestedTopic;

    @Value("${kafka.topic.brew-started}")
    private String brewStartedTopic;

    @Value("${kafka.topic.brew-finished}")
    private String brewFinishedTopic;

    @Value("${kafka.topic.brew-cancel-requested}")
    private String brewCancelRequestedTopic;

    @Value("${kafka.topic.brew-cancelled}")
    private String brewCancelledTopic;

    @Value("${kafka.topic.brew-error}")
    private String brewErrorTopic;


    @Override
    public void configure() throws Exception {
        gateway();
        water();
        beans();
        brew();
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
                .to("direct:add-coffee-order")
                .get("/status/{orderId}").description("Get coffee status").outType(CoffeeOrder.class)
                .param().name("orderId").type(RestParamType.path).description("Order id").endParam()
                .responseMessage().code(200).message("Coffee status").endResponseMessage()
                .responseMessage().code(404).message("Order not found").endResponseMessage()
                .to("direct:get-coffee-status");

        from("direct:add-coffee-order")
                .to("bean-validator:validation")
                .onException(BeanValidationException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .process(exchange -> {
                    BeanValidationException exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, BeanValidationException.class);
                    String violations = exception.getConstraintViolations().stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.joining(", "));
                    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, violations);
                    exchange.getMessage().setBody(problemDetail);
                })
                .end()
                .routeId("make-coffee")
                .log("(${routeId}) () Request received")
                .process(exchange -> {
                    CoffeeMakeRequest request = exchange.getMessage().getBody(CoffeeMakeRequest.class);
                    UUID orderId = UUID.randomUUID();
                    CoffeeOrder coffeeOrder = new CoffeeOrder(orderId, request.beansName(), request.waterTemperature());
                    coffeeOrderRepository.save(coffeeOrder);
                    exchange.getMessage().setHeader("orderId", orderId.toString());
                })
                .log("Order created with id: ${header.orderId}")
                .to("direct:send-kafka-coffee-request")
                .to("direct:make-coffee-response");

        from("direct:send-kafka-coffee-request")
                .routeId("send-kafka-coffee-request")
                .log("(${routeId}) (${header.orderId}) Sending request to Kafka")
                .marshal().json()
                .to("kafka:" + coffeeRequestedTopic);

        from("direct:make-coffee-response")
                .routeId("make-coffee-response")
                .log("(${routeId}) (${header.orderId}) Sending response")
                .process(exchange -> {
                    String orderId = exchange.getMessage().getHeader("orderId", String.class);
                    exchange.getMessage().setBody(new CoffeeMakeResponse(UUID.fromString(orderId)));
                });

        from("kafka:" + coffeeRequestedTopic)
                .routeId("coffee-requested")
                .log("(${routeId}) (${header.orderId}) Coffee request received")
                .unmarshal().json(CoffeeMakeRequest.class)
                .multicast()
                .to("direct:" + waterRequestedTopic)
                .to("direct:" + beansRequestedTopic);

        from("direct:get-coffee-status")
                .routeId("get-coffee-status")
                .log("(${routeId}) (${header.orderId}) Getting coffee status")
                .transacted()
                .process(exchange -> {
                    String orderId = exchange.getMessage().getHeader("orderId", String.class);
                    CoffeeOrder coffeeOrder = coffeeOrderRepository.findByOrderId(UUID.fromString(orderId));
                    if (coffeeOrder == null) {
                        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
                        ProblemDetail orderNotFound = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Order not found");
                        exchange.getMessage().setBody(orderNotFound);
                    } else {
                        exchange.getMessage().setBody(CoffeeOrderResponse.fromCoffeeOrder(coffeeOrder));
                    }
                });

        from("direct:compensate-water-beans")
                .routeId("compensate-water-beans")
                .log("(${routeId}) (${header.orderId}) Starting compensation")
                .transacted()
                .process(exchange -> {
                    String orderId = exchange.getMessage().getHeader("orderId", String.class);
                    CoffeeOrder coffeeOrder = coffeeOrderRepository.findByOrderId(UUID.fromString(orderId));
                    if (coffeeOrder.isBeansCancelled()) {
                        exchange.getMessage().setHeader("cancelWater", true);
                    } else if (coffeeOrder.isWaterCancelled()) {
                        exchange.getMessage().setHeader("cancelBeans", true);
                    }
                })
                .choice()
                .when(header("cancelWater").isEqualTo(true)).to("direct:cancel-water")
                .when(header("cancelBeans").isEqualTo(true)).to("direct:cancel-beans")
                .endChoice();
    }

    public void water() {
        from("direct:" + waterRequestedTopic)
                .routeId(waterRequestedTopic)
                .log("(${routeId}) (${header.orderId}) Sending water request")
                .transacted()
                .process(exchange -> {
                    String orderId = exchange.getMessage().getHeader("orderId", String.class);
                    CoffeeMakeRequest request = exchange.getMessage().getBody(CoffeeMakeRequest.class);
                    WaterPrepareRequest waterRequest = new WaterPrepareRequest(UUID.fromString(orderId), request.waterTemperature());
                    exchange.getMessage().setBody(waterRequest);
                })
                .marshal().json()
                .to("kafka:" + waterRequestedTopic)
                .process(exchange -> {
                    String orderId = exchange.getMessage().getHeader("orderId", String.class);
                    CoffeeOrder coffeeOrder = coffeeOrderRepository.findByOrderId(UUID.fromString(orderId));
                    coffeeOrder.setWaterInProgress();
                    coffeeOrderRepository.save(coffeeOrder);
                });

        from("kafka:" + waterPreparedTopic)
                .routeId(waterPreparedTopic)
                .unmarshal().json(WaterPrepareResponse.class)
                .log("(${routeId}) (${body.brewId}) Water prepared received")
                .transacted()
                .process(exchange -> {
                    WaterPrepareResponse response = exchange.getMessage().getBody(WaterPrepareResponse.class);
                    CoffeeOrder coffeeOrder = coffeeOrderRepository.findByOrderId(response.brewId());
                    coffeeOrder.setWaterReady(response.volume());
                    exchange.getMessage().setHeader("isWaterAndBeansReady", coffeeOrder.isWaterAndBeansReady());
                    exchange.getMessage().setHeader("orderId", coffeeOrder.getOrderId().toString());
                    coffeeOrderRepository.save(coffeeOrder);
                })
                .choice()
                .when(header("isWaterAndBeansReady").isEqualTo(true)).to("direct:" + brewRequestedTopic)
                .endChoice();

        from("kafka:" + waterErrorTopic)
                .routeId(waterErrorTopic)
                .unmarshal().json(WaterErrorMessage.class)
                .log("(${routeId}) (${body.brewId}) Water error received: ${body.message}")
                .transacted()
                .process(exchange -> {
                    WaterErrorMessage errorMessage = exchange.getMessage().getBody(WaterErrorMessage.class);
                    CoffeeOrder coffeeOrder = coffeeOrderRepository.findByOrderId(errorMessage.brewId());
                    coffeeOrder.setWaterCancelled(errorMessage.message());
                    exchange.getMessage().setHeader("orderId", coffeeOrder.getOrderId().toString());
                    coffeeOrderRepository.save(coffeeOrder);
                })
                .to("direct:compensate-water-beans");

        from("direct:cancel-water")
                .routeId("cancel-water")
                .log("(${routeId}) (${header.orderId}) Sending water cancel request")
                .transacted()
                .process(exchange -> {
                    String orderId = exchange.getMessage().getHeader("orderId", String.class);
                    WaterCancelRequest waterCancelRequest = new WaterCancelRequest(UUID.fromString(orderId));
                    exchange.getMessage().setBody(waterCancelRequest);
                })
                .marshal().json()
                .to("kafka:" + waterCancelRequestedTopic);

        from("kafka:" + waterCancelledTopic)
                .routeId(waterCancelledTopic)
                .unmarshal().json(WaterCancelRequest.class)
                .log("(${routeId}) (${body.brewId}) Water cancelled received")
                .transacted()
                .process(exchange -> {
                    WaterCancelRequest request = exchange.getMessage().getBody(WaterCancelRequest.class);
                    CoffeeOrder coffeeOrder = coffeeOrderRepository.findByOrderId(request.brewId());
                    coffeeOrder.setWaterCancelled();
                    coffeeOrderRepository.save(coffeeOrder);
                });

    }

    public void beans() {
        from("direct:" + beansRequestedTopic)
                .routeId(beansRequestedTopic)
                .log("(${routeId}) (${header.orderId}) Sending beans request")
                .transacted()
                .process(exchange -> {
                    String orderId = exchange.getMessage().getHeader("orderId", String.class);
                    CoffeeMakeRequest request = exchange.getMessage().getBody(CoffeeMakeRequest.class);
                    BeansPrepareRequest beansRequest = new BeansPrepareRequest(UUID.fromString(orderId), request.beansName());
                    exchange.getMessage().setBody(beansRequest);
                })
                .marshal().json()
                .to("kafka:" + beansRequestedTopic)
                .process(exchange -> {
                    String orderId = exchange.getMessage().getHeader("orderId", String.class);
                    CoffeeOrder coffeeOrder = coffeeOrderRepository.findByOrderId(UUID.fromString(orderId));
                    coffeeOrder.setBeansInProgress();
                    coffeeOrderRepository.save(coffeeOrder);
                });

        from("kafka:" + beansPreparedTopic)
                .routeId(beansPreparedTopic)
                .unmarshal().json(BeansPrepareResponse.class)
                .log("(${routeId}) (${body.brewId}) Beans prepared received")
                .transacted()
                .process(exchange -> {
                    BeansPrepareResponse response = exchange.getMessage().getBody(BeansPrepareResponse.class);
                    CoffeeOrder coffeeOrder = coffeeOrderRepository.findByOrderId(response.brewId());
                    coffeeOrder.setBeansReady(response.weight());
                    exchange.getMessage().setHeader("isWaterAndBeansReady", coffeeOrder.isWaterAndBeansReady());
                    exchange.getMessage().setHeader("orderId", coffeeOrder.getOrderId().toString());
                    coffeeOrderRepository.save(coffeeOrder);
                })
                .choice()
                .when(header("isWaterAndBeansReady").isEqualTo(true)).to("direct:" + brewRequestedTopic)
                .endChoice();

        from("kafka:" + beansErrorTopic)
                .routeId(beansErrorTopic)
                .unmarshal().json(BeansErrorMessage.class)
                .log("(${routeId}) (${body.brewId}) Beans error received: ${body.message}")
                .transacted()
                .process(exchange -> {
                    BeansErrorMessage errorMessage = exchange.getMessage().getBody(BeansErrorMessage.class);
                    CoffeeOrder coffeeOrder = coffeeOrderRepository.findByOrderId(errorMessage.brewId());
                    coffeeOrder.setBeansCancelled(errorMessage.message());
                    exchange.getMessage().setHeader("orderId", coffeeOrder.getOrderId().toString());
                    coffeeOrderRepository.save(coffeeOrder);
                })
                .to("direct:compensate-water-beans");

        from("direct:cancel-beans")
                .routeId("cancel-beans")
                .log("(${routeId}) (${header.orderId}) Sending beans cancel request")
                .transacted()
                .process(exchange -> {
                    String orderId = exchange.getMessage().getHeader("orderId", String.class);
                    BeansCancelRequest beansCancelRequest = new BeansCancelRequest(UUID.fromString(orderId));
                    exchange.getMessage().setBody(beansCancelRequest);
                })
                .marshal().json()
                .to("kafka:" + beansCancelRequestedTopic);

        from("kafka:" + beansCancelledTopic)
                .routeId(beansCancelledTopic)
                .unmarshal().json(BeansCancelResponse.class)
                .log("(${routeId}) (${body.brewId}) Beans cancelled received")
                .transacted()
                .process(exchange -> {
                    BeansCancelResponse request = exchange.getMessage().getBody(BeansCancelResponse.class);
                    CoffeeOrder coffeeOrder = coffeeOrderRepository.findByOrderId(request.brewId());
                    coffeeOrder.setBeansCancelled();
                    coffeeOrderRepository.save(coffeeOrder);
                });
    }

    private void brew() {
        from("direct:" + brewRequestedTopic)
                .routeId(brewRequestedTopic)
                .log("(${routeId}) (${header.orderId}) Sending brew request")
                .transacted()
                .process(exchange -> {
                    String orderId = exchange.getMessage().getHeader("orderId", String.class);
                    CoffeeOrder coffeeOrder = coffeeOrderRepository.findByOrderId(UUID.fromString(orderId));
                    BrewStartRequest brewStartRequest = new BrewStartRequest(UUID.fromString(orderId), coffeeOrder.getWaterVolume(), coffeeOrder.getBeansWeight());
                    exchange.getMessage().setBody(brewStartRequest);
                })
                .marshal().json()
                .to("kafka:" + brewRequestedTopic);

        from("kafka:" + brewStartedTopic)
                .routeId(brewStartedTopic)
                .unmarshal().json(BrewStartResponse.class)
                .log("(${routeId}) (${body.brewId}) Brew started received")
                .transacted()
                .process(exchange -> {
                    BrewStartResponse response = exchange.getMessage().getBody(BrewStartResponse.class);
                    CoffeeOrder coffeeOrder = coffeeOrderRepository.findByOrderId(response.brewId());
                    coffeeOrder.setBrewInProgress(response.timeOfBrewing());
                    coffeeOrderRepository.save(coffeeOrder);
                });

        from("kafka:" + brewFinishedTopic)
                .routeId(brewFinishedTopic)
                .unmarshal().json(BrewFinishResponse.class)
                .log("(${routeId}) (${body.brewId}) Brew finished received")
                .transacted()
                .process(exchange -> {
                    BrewFinishResponse response = exchange.getMessage().getBody(BrewFinishResponse.class);
                    CoffeeOrder coffeeOrder = coffeeOrderRepository.findByOrderId(response.brewId());
                    coffeeOrder.setBrewReady();
                    coffeeOrderRepository.save(coffeeOrder);
                });
    }
}
