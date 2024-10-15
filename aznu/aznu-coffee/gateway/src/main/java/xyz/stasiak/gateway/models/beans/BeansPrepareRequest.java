package xyz.stasiak.gateway.models.beans;

import java.util.UUID;

public record BeansPrepareRequest(UUID brewId, String name, int weight) {
}
