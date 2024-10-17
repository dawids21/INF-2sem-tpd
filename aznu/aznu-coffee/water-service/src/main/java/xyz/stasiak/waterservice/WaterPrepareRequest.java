package xyz.stasiak.waterservice;

import java.util.UUID;

public record WaterPrepareRequest(UUID brewId, int temperature) {
}
