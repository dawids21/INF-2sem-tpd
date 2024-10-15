package xyz.stasiak.gateway.models.water;

import java.util.UUID;

public record WaterPrepareRequest(UUID brewId, int volume, int temperature) {
}
