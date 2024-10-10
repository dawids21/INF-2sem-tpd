package xyz.stasiak.beansclientservice;

import java.util.UUID;

public record BeansPrepareResponse(UUID brewId, String name, int weight) {
}
