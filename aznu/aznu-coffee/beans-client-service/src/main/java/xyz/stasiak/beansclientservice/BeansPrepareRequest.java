package xyz.stasiak.beansclientservice;

import java.util.UUID;

public record BeansPrepareRequest(UUID brewId, String name) {
}
