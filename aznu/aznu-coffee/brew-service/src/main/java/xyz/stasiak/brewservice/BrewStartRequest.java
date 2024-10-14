package xyz.stasiak.brewservice;

import java.util.UUID;

public record BrewStartRequest(UUID brewId, int waterVolume, int beansWeight) {
}
