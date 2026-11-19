package com.portifolio.api.service.impl;

import com.portifolio.api.dto.request.ChangePasswordRequest;
import com.portifolio.api.dto.request.LoginRequest;
import com.portifolio.api.entity.AdminUser;
import com.portifolio.api.exception.BusinessRuleException;
import com.portifolio.api.exception.TooManyRequestsException;
import com.portifolio.api.ratelimit.LoginAttemptLimiter;
import com.portifolio.api.repository.AdminUserRepository;
import com.portifolio.api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private AdminUserRepository repository;

    @Mock
    private LoginAttemptLimiter limiter;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder(4);
    private AuthServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AuthServiceImpl(authenticationManager, jwtService, repository, encoder, limiter);
    }

    @Test
    void loginIsRejectedWhenClientIsBlocked() {
        when(limiter.isBlocked("ip")).thenReturn(true);

        assertThatThrownBy(() -> service.login(new LoginRequest("admin", "x"), "ip"))
                .isInstanceOf(TooManyRequestsException.class);
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void failedLoginIsRecordedAndRethrown() {
        when(limiter.isBlocked("ip")).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> service.login(new LoginRequest("admin", "wrong"), "ip"))
                .isInstanceOf(BadCredentialsException.class);
        verify(limiter).recordFailure("ip");
    }

    @Test
    void changePasswordRejectsWrongCurrentPassword() {
        AdminUser admin = new AdminUser("admin", encoder.encode("senha-atual"));
        when(repository.findByUsername("admin")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> service.changePassword("admin", new ChangePasswordRequest("errada", "nova-senha-123")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("incorreta");
        verify(repository, never()).save(any());
    }

    @Test
    void changePasswordStoresNewHash() {
        AdminUser admin = new AdminUser("admin", encoder.encode("senha-atual"));
        when(repository.findByUsername("admin")).thenReturn(Optional.of(admin));

        service.changePassword("admin", new ChangePasswordRequest("senha-atual", "nova-senha-123"));

        assertThat(encoder.matches("nova-senha-123", admin.getPasswordHash())).isTrue();
        verify(repository).save(admin);
    }

    @Test
    void changePasswordBumpsTokenVersionToInvalidateOldTokens() {
        AdminUser admin = new AdminUser("admin", encoder.encode("senha-atual"));
        when(repository.findByUsername("admin")).thenReturn(Optional.of(admin));
        long before = admin.getTokenVersion();

        service.changePassword("admin", new ChangePasswordRequest("senha-atual", "nova-senha-123"));

        assertThat(admin.getTokenVersion()).isEqualTo(before + 1);
    }

    @Test
    void rejectedPasswordChangeKeepsTokenVersion() {
        AdminUser admin = new AdminUser("admin", encoder.encode("senha-atual"));
        when(repository.findByUsername("admin")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> service.changePassword("admin", new ChangePasswordRequest("errada", "nova-senha-123")))
                .isInstanceOf(BusinessRuleException.class);

        assertThat(admin.getTokenVersion()).isZero();
    }

    @Test
    void loginIssuesTokenWithCurrentTokenVersion() {
        AdminUser admin = new AdminUser("admin", encoder.encode("senha-atual"));
        admin.changePasswordHash(encoder.encode("outra-senha-1")); // versão 1
        when(limiter.isBlocked("ip")).thenReturn(false);
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken("admin", null));
        when(repository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(jwtService.generateToken("admin", 1L)).thenReturn("jwt");
        when(jwtService.getExpirationSeconds()).thenReturn(1800L);

        var response = service.login(new LoginRequest("admin", "outra-senha-1"), "ip");

        assertThat(response.token()).isEqualTo("jwt");
        verify(limiter).reset("ip");
    }

    @Test
    void changePasswordRejectsSamePassword() {
        AdminUser admin = new AdminUser("admin", encoder.encode("mesma-senha-1"));
        when(repository.findByUsername("admin")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> service.changePassword("admin", new ChangePasswordRequest("mesma-senha-1", "mesma-senha-1")))
                .isInstanceOf(BusinessRuleException.class);
    }
}