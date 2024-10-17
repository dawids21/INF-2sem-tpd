package xyz.stasiak.gateway.models.brew;

import java.util.UUID;

public record BrewStartRequest(UUID brewId, int waterVolume, int beansWeight) {
}
