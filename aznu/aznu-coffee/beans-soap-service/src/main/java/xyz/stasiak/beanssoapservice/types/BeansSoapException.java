package xyz.stasiak.beanssoapservice.types;

import java.util.UUID;

public class BeansSoapException extends RuntimeException {
  private final UUID brewId;

  public BeansSoapException(UUID brewId, String message) {
    super(message);
    this.brewId = brewId;
  }

  public BeansSoapException(UUID brewId, String message, Throwable cause) {
    super(message, cause);
    this.brewId = brewId;
  }

  public UUID getBrewId() {
    return brewId;
  }
}
