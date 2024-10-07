package org.bp.travel;


import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.impl.saga.InMemorySagaService;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.model.rest.RestBindingMode;
import org.bp.travel.model.TravelBookingRequest;
import org.bp.travel.model.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static org.apache.camel.model.rest.RestParamType.body;

@Component
public class TravelBookingService extends RouteBuilder {

    @Autowired
    CamelContext camelContext;

    @Autowired
    BookingIdentifierService bookingIdentifierService;

    @Override
    public void configure() throws Exception {
        camelContext.addService(new InMemorySagaService());

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .enableCORS(true)
                .contextPath("/api")
                // turn on swagger api-doc
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Travel booking API")
                .apiProperty("api.version", "1.0.0");

        rest("/travel").description("Travel booking REST service")
                .consumes("application/json")
                .produces("application/json")
                .post("/booking").description("Book a travel").type(TravelBookingRequest.class).outType(org.bp.payment.PaymentResponse.class)
                .param().name("body").type(body).description("The travel to book").endParam()
                .responseMessage().code(200).message("Travel successfully booked").endResponseMessage()
                .to("direct:bookTravel");

        from("direct:bookTravel").routeId("bookTravel")
                .log("bookTravel fired")
                .process(
                        (exchange) -> {
                            exchange.setProperty("paymentRequest",
                                    Utils.preparePaymentRequest(exchange.getMessage().getBody(TravelBookingRequest.class)));
                            exchange.setProperty("travelBookingId", bookingIdentifierService.generateBookingId());
                        }
                )
                .saga()
                .multicast()
                .parallelProcessing()
                .aggregationStrategy(
                        (prevEx, currentEx) -> {
                            if (currentEx.getException() != null)
                                return currentEx;
                            if (prevEx != null && prevEx.getException() != null)
                                return prevEx;
                            org.bp.payment.PaymentRequest paymentRequest;
                            if (prevEx == null)
                                paymentRequest = currentEx.getProperty("paymentRequest",
                                        org.bp.payment.PaymentRequest.class);
                            else
                                paymentRequest = prevEx.getMessage().getBody(org.bp.payment.PaymentRequest.class);
                            Object body = currentEx.getMessage().getBody();
                            BigDecimal cost;
                            if (body instanceof org.bp.BookingInfo)
                                cost = ((org.bp.BookingInfo) body).getCost();
                            else if (body instanceof org.bp.flight.BookFligthResponse)
                                cost = ((org.bp.flight.BookFligthResponse) body).getReturn().getCost();
                            else
                                return prevEx;
                            paymentRequest.getAmount().setValue(
                                    paymentRequest.getAmount().getValue().add(cost));
                            currentEx.getMessage().setBody(paymentRequest);
                            return currentEx;
                        }
                )
                .to("direct:bookFlight")
                .to("direct:bookHotel")
                .end()
                .process(
                        (currentEx) -> {
                            currentEx.getMessage().setBody(currentEx.getProperty("paymentRequest",
                                    org.bp.payment.PaymentRequest.class));
                        }
                )
                .to("direct:payment")
                .removeHeaders("Camel*")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
        ;
        JaxbDataFormat jaxbFlight = new
                JaxbDataFormat(org.bp.flight.BookFligthResponse.class.getPackage().getName());

        from("direct:bookFlight").routeId("bookFlight")
                .log("bookFlight fired")
                .saga()
                .propagation(SagaPropagation.MANDATORY)
                .compensation("direct:cancelFlight").option("travelBookingId",
                        simple("${exchangeProperty.travelBookingId}"))
                .process((exchange) ->
                {
                    exchange.getMessage().setBody(
                            Utils.prepareFlightBookingRequest(exchange.getMessage().getBody(TravelBookingRequest.class)));
                })
                .marshal(jaxbFlight)
                .to("spring-ws:http://localhost:8081/soap-api/service/fligth")
                .to("stream:out")
                .unmarshal(jaxbFlight)
                .process((exchange) -> {
                    org.bp.flight.BookFligthResponse bookFlightResponse =
                            exchange.getMessage().getBody(org.bp.flight.BookFligthResponse.class);
                    String travelBookingId = exchange.getProperty("travelBookingId", String.class);
                    bookingIdentifierService.assignFlightBookingId(travelBookingId,
                            bookFlightResponse.getReturn().getId());
                });

        JaxbDataFormat jaxbHotel = new JaxbDataFormat(org.bp.BookingInfo.class.getPackage().getName());
        from("direct:bookHotel").routeId("bookHotel")
                .log("bookHotel fired")
                .saga()
                .propagation(SagaPropagation.MANDATORY)
                .compensation("direct:cancelHotel").option("travelBookingId",
                        simple("${exchangeProperty.travelBookingId}"))
                .process((exchange) ->
                {
                    exchange.getMessage().setBody(
                            Utils.prepareHotelBookingRequest(exchange.getMessage().getBody(TravelBookingRequest.class)));

                })
                .marshal(jaxbHotel)
                .to("spring-ws:http://localhost:8080/soap-api/service/hotel")
                .to("stream:out")
                .unmarshal(jaxbHotel)
                .process((exchange) -> {
                    org.bp.BookingInfo bookhotelResponse = exchange.getMessage().getBody(org.bp.BookingInfo.class);
                    String travelBookingId = exchange.getProperty("travelBookingId", String.class);
                    bookingIdentifierService.assignHotelBookingId(travelBookingId, bookhotelResponse.getId());
                });

        from("direct:payment").routeId("payment")
                .log("payment fired")
                .marshal().json()
                .removeHeaders("CamelHttp*")
                .to("rest:post:payment?host=localhost:8083")
//                .to("stream:out")
                .unmarshal().json()
        ;

        from("direct:cancelHotel").routeId("cancelHotel")
                .log("cancelHotel fired")
                .process((exchange) -> {
                    String travelBookingId = exchange.getMessage().getHeader("travelBookingId", String.class);
                    int hotelBookingId = bookingIdentifierService.getHotelBookingId(travelBookingId);
                    org.bp.CancelBookingRequest cancelHotelFlightRequest = new org.bp.CancelBookingRequest();
                    cancelHotelFlightRequest.setBookingId(hotelBookingId);
                    exchange.getMessage().setBody(cancelHotelFlightRequest);
                })
                .marshal(jaxbHotel)
                .to("spring-ws:http://localhost:8080/soap-api/service/hotel")
                .to("stream:out")
                .unmarshal(jaxbHotel)
        ;
        from("direct:cancelFlight").routeId("cancelFlight")
                .log("cancelFlight fired")
                .process((exchange) -> {
                    String travelBookingId = exchange.getMessage().getHeader("travelBookingId", String.class);
                    int flightBookingId = bookingIdentifierService.getFlightBookingId(travelBookingId);
                    org.bp.flight.CancelBooking cancelBookFlightRequest = new org.bp.flight.CancelBooking();
                    cancelBookFlightRequest.setArg0(flightBookingId);
                    exchange.getMessage().setBody(cancelBookFlightRequest);
                })
                .marshal(jaxbFlight)
                .to("spring-ws:http://localhost:8081/soap-api/service/fligth")
                .to("stream:out")
                .unmarshal(jaxbFlight);


    }
}
