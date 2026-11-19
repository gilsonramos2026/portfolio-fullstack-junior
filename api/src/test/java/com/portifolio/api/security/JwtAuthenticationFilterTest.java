package com.portifolio.api.security;

import com.portifolio.api.config.SecurityProperties;
import com.portifolio.api.entity.AdminUser;
import com.portifolio.api.repository.AdminUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("0123456789-0123456789-0123456789-0123456789".getBytes());

    private final JwtService jwtService = new JwtService(
            new SecurityProperties(SECRET, 30, List.of("http://localhost:3000"))
    );
    private final AdminUserRepository repository = mock(AdminUserRepository.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, repository);

    @BeforeEach
    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private AdminUser mockAdmin(String username, long tokenVersion) {
        AdminUser admin = mock(AdminUser.class);
        when(admin.getUsername()).thenReturn(username);
        when(admin.getTokenVersion()).thenReturn(tokenVersion);
        return admin;
    }

    @Test
    void setsAuthenticationWhenTokenAndVersionAreValid() throws Exception {
        AdminUser admin = mockAdmin("admin", 2L);
        when(repository.findByUsername("admin")).thenReturn(Optional.of(admin));

        String token = jwtService.generateToken("admin", 2L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("admin");
    }

    @Test
    void doesNotAuthenticateWhenTokenVersionDoesNotMatch() throws Exception {
        AdminUser admin = mockAdmin("admin", 3L);
        when(repository.findByUsername("admin")).thenReturn(Optional.of(admin));

        String token = jwtService.generateToken("admin", 2L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doesNotAuthenticateWhenHeaderIsMissingOrInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-invalido");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}