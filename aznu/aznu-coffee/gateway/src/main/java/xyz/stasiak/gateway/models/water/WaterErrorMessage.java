package xyz.stasiak.gateway.models.water;

import java.util.UUID;

public record WaterErrorMessage(UUID brewId, String message) {
}
