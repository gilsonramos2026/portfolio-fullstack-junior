package com.portifolio.api.ratelimit;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class ContactRateLimiterTest {

    /** Relógio controlável para simular a passagem do tempo. */
    private static final class MutableClock extends Clock {
        private Instant now = Instant.parse("2026-01-01T12:00:00Z");

        void advance(Duration duration) {
            now = now.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return now;
        }
    }

    @Test
    void blocksAfterMaxRequestsWithinWindow() {
        ContactRateLimiter limiter = new ContactRateLimiter(new MutableClock());

        for (int i = 0; i < ContactRateLimiter.MAX_REQUESTS; i++) {
            assertThat(limiter.tryAcquire("1.1.1.1")).isTrue();
        }

        assertThat(limiter.tryAcquire("1.1.1.1")).isFalse();
        assertThat(limiter.tryAcquire("2.2.2.2")).isTrue();
    }

    @Test
    void allowsAgainAfterWindowPasses() {
        MutableClock clock = new MutableClock();
        ContactRateLimiter limiter = new ContactRateLimiter(clock);

        for (int i = 0; i < ContactRateLimiter.MAX_REQUESTS; i++) {
            assertThat(limiter.tryAcquire("1.1.1.1")).isTrue();
        }
        assertThat(limiter.tryAcquire("1.1.1.1")).isFalse();

        clock.advance(ContactRateLimiter.WINDOW.plusSeconds(1));

        assertThat(limiter.tryAcquire("1.1.1.1")).isTrue();
    }
}