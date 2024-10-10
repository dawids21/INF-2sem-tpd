package xyz.stasiak.gateway;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CoffeeService extends RouteBuilder {
    @Value("${kafka.topic.water-requested}")
    private String waterRequestedTopic;

    @Value("${kafka.topic.water-prepared}")
    private String waterPreparedTopic;

    @Value("${kafka.topic.water-cancel-requested}")
    private String waterCancelRequestedTopic;

    @Value("${kafka.topic.water-cancelled}")
    private String waterCancelledTopic;

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .enableCORS(true)
                .contextPath("/api")
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
                .to("direct:make-coffee");

        from("direct:make-coffee")
                .log("Making coffee: ${body}")
                .process(exchange -> {
                    exchange.getMessage().setBody(new CoffeeMakeResponse(UUID.randomUUID()));
                });
    }
}
