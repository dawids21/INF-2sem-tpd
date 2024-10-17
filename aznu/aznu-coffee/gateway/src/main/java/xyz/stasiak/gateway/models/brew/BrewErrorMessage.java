package xyz.stasiak.gateway.models.brew;

import java.util.UUID;

public record BrewErrorMessage(UUID brewId, String message) {
}
