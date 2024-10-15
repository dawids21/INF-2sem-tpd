package xyz.stasiak.gateway.models.beans;

import java.util.UUID;

public record BeansPrepareResponse(UUID brewId, String name, int weight) {
}
