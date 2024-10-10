package xyz.stasiak.waterservice;

import java.util.UUID;

public class WaterException extends RuntimeException {
    private final UUID brewId;

    public WaterException(UUID brewId, String message) {
        super(message);
        this.brewId = brewId;
    }

    public WaterException(UUID brewId, String message, Throwable cause) {
        super(message, cause);
        this.brewId = brewId;
    }

    public UUID getBrewId() {
        return brewId;
    }
}
