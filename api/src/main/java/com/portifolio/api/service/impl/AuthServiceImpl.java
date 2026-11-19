package com.portifolio.api.service.impl;

import com.portifolio.api.dto.request.ChangePasswordRequest;
import com.portifolio.api.dto.request.LoginRequest;
import com.portifolio.api.dto.response.LoginResponse;
import com.portifolio.api.entity.AdminUser;
import com.portifolio.api.exception.BusinessRuleException;
import com.portifolio.api.exception.TooManyRequestsException;
import com.portifolio.api.ratelimit.LoginAttemptLimiter;
import com.portifolio.api.repository.AdminUserRepository;
import com.portifolio.api.security.JwtService;
import com.portifolio.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptLimiter loginLimiter;

    @Override
    public LoginResponse login(LoginRequest request, String clientKey) {
        if (loginLimiter.isBlocked(clientKey)) {
            throw new TooManyRequestsException("Muitas tentativas de login. Tente novamente em alguns minutos");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.username(),
                            request.password()
                    )
            );
        } catch (AuthenticationException ex) {
            loginLimiter.recordFailure(clientKey);
            throw ex;
        }

        loginLimiter.reset(clientKey);
        String username = authentication.getName();
        long tokenVersion = adminUserRepository.findByUsername(username)
                .map(AdminUser::getTokenVersion)
                .orElse(0L);

        return new LoginResponse(
                jwtService.generateToken(username, tokenVersion),
                "Bearer",
                jwtService.getExpirationSeconds(),
                username
        );
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        AdminUser admin = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessRuleException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.currentPassword(), admin.getPasswordHash())) {
            throw new BusinessRuleException("Senha atual incorreta");
        }

        if (request.currentPassword().equals(request.newPassword())) {
            throw new BusinessRuleException("A nova senha deve ser diferente da atual");
        }

        admin.changePasswordHash(passwordEncoder.encode(request.newPassword()));
        adminUserRepository.save(admin);
    }
}