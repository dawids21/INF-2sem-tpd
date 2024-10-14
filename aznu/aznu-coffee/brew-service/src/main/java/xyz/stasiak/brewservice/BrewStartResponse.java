package xyz.stasiak.brewservice;

import java.util.UUID;

public record BrewStartResponse(UUID brewId, int timeOfBrewing) {
}
