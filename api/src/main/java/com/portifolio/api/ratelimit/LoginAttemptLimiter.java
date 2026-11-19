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
 * Protege o login contra força bruta: após {@value #MAX_FAILURES} falhas em {@code WINDOW}
 * o IP fica bloqueado até a janela expirar. Um login bem-sucedido zera o contador.
 */
@Component
public class LoginAttemptLimiter {

    static final int MAX_FAILURES = 5;
    static final Duration WINDOW = Duration.ofMinutes(15);

    /** Acima disso, entradas expiradas são descartadas para a memória não crescer com muitos IPs distintos. */
    static final int MAX_TRACKED_KEYS = 10_000;

    private final Map<String, Deque<Instant>> failures = new ConcurrentHashMap<>();
    private final Clock clock;

    public LoginAttemptLimiter() {
        this(Clock.systemUTC());
    }

    LoginAttemptLimiter(Clock clock) {
        this.clock = clock;
    }

    public boolean isBlocked(String key) {
        Deque<Instant> window = failures.get(key);
        if (window == null) {
            return false;
        }
        synchronized (window) {
            prune(window);
            return window.size() >= MAX_FAILURES;
        }
    }

    public void recordFailure(String key) {
        Deque<Instant> window = failures.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (window) {
            prune(window);
            window.addLast(clock.instant());
        }
        if (failures.size() > MAX_TRACKED_KEYS) {
            evictExpired();
        }
    }

    int trackedKeys() {
        return failures.size();
    }

    private void evictExpired() {
        Instant threshold = clock.instant().minus(WINDOW);
        failures.entrySet().removeIf(entry -> {
            Deque<Instant> window = entry.getValue();
            synchronized (window) {
                return window.isEmpty() || window.peekLast().isBefore(threshold);
            }
        });
    }

    public void reset(String key) {
        failures.remove(key);
    }

    private void prune(Deque<Instant> window) {
        Instant threshold = clock.instant().minus(WINDOW);
        while (!window.isEmpty() && window.peekFirst().isBefore(threshold)) {
            window.pollFirst();
        }
    }
}