package xyz.stasiak.gateway.models.brew;

import java.util.UUID;

public record BrewStartResponse(UUID brewId, int timeOfBrewing) {
}
