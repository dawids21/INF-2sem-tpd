package xyz.stasiak.gateway;

import java.util.Set;
import java.util.UUID;

public record CoffeeOrderResponse(UUID brewId,
                                  Integer beansWeight,
                                  String beansName,
                                  Integer waterVolume,
                                  Integer waterTemperature,
                                  Integer timeOfBrewing,
                                  String brewStatus,
                                  String waterStatus,
                                  String beansStatus,
                                  String status,
                                  Set<String> reasons) {

    public static CoffeeOrderResponse fromCoffeeOrder(CoffeeOrder coffeeOrder) {
        return new CoffeeOrderResponse(coffeeOrder.getOrderId(),
                coffeeOrder.getBeansWeight(),
                coffeeOrder.getBeansName(),
                coffeeOrder.getWaterVolume(),
                coffeeOrder.getWaterTemperature(),
                coffeeOrder.getTimeOfBrewing(),
                coffeeOrder.getBrewStatus().toString(),
                coffeeOrder.getWaterStatus().toString(),
                coffeeOrder.getBeansStatus().toString(),
                coffeeOrder.getStatus().toString(),
                coffeeOrder.getReasons());
    }
}
