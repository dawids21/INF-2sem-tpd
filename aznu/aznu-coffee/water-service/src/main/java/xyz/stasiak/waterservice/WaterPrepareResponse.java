package xyz.stasiak.waterservice;

import java.util.UUID;

public record WaterPrepareResponse(UUID brewId, int volume, int temperature) {
}
