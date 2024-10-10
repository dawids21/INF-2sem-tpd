package xyz.stasiak.beansclientservice;

import java.util.UUID;

public record BeansErrorMessage(UUID brewId, String message) {
}
