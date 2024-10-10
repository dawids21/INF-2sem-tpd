package xyz.stasiak.waterservice;

import java.util.UUID;

public record WaterErrorMessage(UUID brewId, String message) {
}
