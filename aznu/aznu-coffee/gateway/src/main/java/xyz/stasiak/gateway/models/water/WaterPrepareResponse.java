package xyz.stasiak.gateway.models.water;

import java.util.UUID;

public record WaterPrepareResponse(UUID brewId, int volume, int temperature) {
}
