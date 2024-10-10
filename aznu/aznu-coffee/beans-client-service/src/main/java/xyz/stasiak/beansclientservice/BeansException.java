package xyz.stasiak.beansclientservice;

import java.util.UUID;

public class BeansException extends RuntimeException {
    private final UUID brewId;

    public BeansException(UUID brewId, String message) {
        super(message);
        this.brewId = brewId;
    }

    public BeansException(UUID brewId, String message, Throwable cause) {
        super(message, cause);
        this.brewId = brewId;
    }

    public UUID getBrewId() {
        return brewId;
    }
}
