package com.portifolio.api.security;

import com.portifolio.api.config.ProxyProperties;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClientIpResolverTest {

    private static final ProxyProperties PRIVATE_NETWORKS = new ProxyProperties(
            List.of("127.0.0.0/8", "10.0.0.0/8", "172.16.0.0/12", "192.168.0.0/16")
    );

    private static MockHttpServletRequest request(String peer, String forwardedFor) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(peer);
        if (forwardedFor != null) {
            request.addHeader("X-Forwarded-For", forwardedFor);
        }
        return request;
    }

    @Test
    void usesForwardedHeaderWhenPeerIsTrustedProxy() {
        ClientIpResolver resolver = new ClientIpResolver(PRIVATE_NETWORKS);

        assertThat(resolver.resolve(request("172.18.0.5", "203.0.113.7")))
                .isEqualTo("203.0.113.7");
    }

    @Test
    void ignoresForwardedHeaderFromUntrustedPeer() {
        ClientIpResolver resolver = new ClientIpResolver(PRIVATE_NETWORKS);

        assertThat(resolver.resolve(request("198.51.100.20", "1.2.3.4")))
                .isEqualTo("198.51.100.20");
    }

    @Test
    void fallsBackToPeerWhenHeaderIsMissingOrInvalid() {
        ClientIpResolver resolver = new ClientIpResolver(PRIVATE_NETWORKS);

        assertThat(resolver.resolve(request("10.0.0.8", null))).isEqualTo("10.0.0.8");
        assertThat(resolver.resolve(request("10.0.0.8", "<script>alert(1)</script>")))
                .isEqualTo("10.0.0.8");
        assertThat(resolver.resolve(request("10.0.0.8", "   "))).isEqualTo("10.0.0.8");
    }

    @Test
    void usesFirstEntryOfAForwardedChain() {
        ClientIpResolver resolver = new ClientIpResolver(PRIVATE_NETWORKS);

        assertThat(resolver.resolve(request("10.0.0.8", "203.0.113.7, 10.1.1.1")))
                .isEqualTo("203.0.113.7");
    }

    @Test
    void trustsNobodyWhenListIsEmpty() {
        ClientIpResolver resolver = new ClientIpResolver(new ProxyProperties(null));

        assertThat(resolver.resolve(request("10.0.0.8", "203.0.113.7")))
                .isEqualTo("10.0.0.8");
    }
}