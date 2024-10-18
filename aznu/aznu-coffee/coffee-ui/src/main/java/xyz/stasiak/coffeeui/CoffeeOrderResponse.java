package xyz.stasiak.coffeeui;

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
}
