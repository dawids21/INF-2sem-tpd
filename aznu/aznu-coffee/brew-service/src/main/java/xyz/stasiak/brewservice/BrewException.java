package xyz.stasiak.brewservice;

import java.util.UUID;

public class BrewException extends RuntimeException {
    private final UUID brewId;

    public BrewException(UUID brewId, String message) {
        super(message);
        this.brewId = brewId;
    }

    public BrewException(UUID brewId, String message, Throwable cause) {
        super(message, cause);
        this.brewId = brewId;
    }

    public UUID getBrewId() {
        return brewId;
    }
}
