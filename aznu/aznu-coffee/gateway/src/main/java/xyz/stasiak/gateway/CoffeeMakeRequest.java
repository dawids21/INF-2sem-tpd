package xyz.stasiak.gateway;

import lombok.Data;

public record CoffeeMakeRequest(int beansWeight, String beansName, int waterVolume, int waterTemperature) {
}
