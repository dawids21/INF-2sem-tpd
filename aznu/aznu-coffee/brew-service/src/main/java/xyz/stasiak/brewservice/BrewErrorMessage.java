package xyz.stasiak.brewservice;

import java.util.UUID;

public record BrewErrorMessage(UUID brewId, String message) {
}
