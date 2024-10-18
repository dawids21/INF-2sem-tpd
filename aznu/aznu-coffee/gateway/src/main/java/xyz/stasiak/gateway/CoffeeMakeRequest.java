package xyz.stasiak.gateway;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CoffeeMakeRequest(
        @NotBlank(message = "Beans name must not be blank")
        String beansName,
        @Positive(message = "Water temperature must be positive")
        Integer waterTemperature
) {
}
