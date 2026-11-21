package com.portifolio.api.ratelimit;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limitador em memória (janela deslizante) para o envio de recados: no máximo
 * {@value #MAX_REQUESTS} por {@code WINDOW} por chave (IP).
 */
@Component
public class ContactRateLimiter {

    static final int MAX_REQUESTS = 5;
    static final Duration WINDOW = Duration.ofHours(1);

    private final Map<String, Deque<Instant>> hits = new ConcurrentHashMap<>();
    private final Clock clock;

    public ContactRateLimiter() {
        this(Clock.systemUTC());
    }

    ContactRateLimiter(Clock clock) {
        this.clock = clock;
    }

    public boolean tryAcquire(String key) {
        if (key == null || key.isBlank()) {
            return false;
        }

        Instant now = clock.instant();
        Instant threshold = now.minus(WINDOW);
        boolean[] allowed = {false};

        hits.compute(key, (k, deque) -> {
            Deque<Instant> window = deque == null ? new ArrayDeque<>() : deque;

            while (!window.isEmpty() && window.peekFirst().isBefore(threshold)) {
                window.pollFirst();
            }

            if (window.size() < MAX_REQUESTS) {
                window.addLast(now);
                allowed[0] = true;
            }

            return window.isEmpty() ? null : window;
        });

        if (hits.size() > 10_000) {
            hits.entrySet().removeIf(e -> e.getValue().isEmpty()
                    || e.getValue().peekLast().isBefore(threshold));
        }

        return allowed[0];
    }
}