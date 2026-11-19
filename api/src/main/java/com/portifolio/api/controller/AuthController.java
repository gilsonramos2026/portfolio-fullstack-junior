package com.portifolio.api.controller;

import com.portifolio.api.dto.request.ChangePasswordRequest;
import com.portifolio.api.dto.request.LoginRequest;
import com.portifolio.api.dto.response.LoginResponse;
import com.portifolio.api.dto.response.MeResponse;
import com.portifolio.api.security.ClientIpResolver;
import com.portifolio.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Autenticação do administrador via JWT")
public class AuthController {

    private final AuthService authService;
    private final ClientIpResolver clientIpResolver;

    @PostMapping("/login")
    @ApiResponse(responseCode = "401", description = "Usuário ou senha inválidos")
    @ApiResponse(responseCode = "429", description = "Muitas tentativas de login deste IP (bloqueio de 15 minutos)")
    @Operation(summary = "Autentica o administrador e devolve um token JWT (bloqueia o IP após 5 falhas em 15 minutos)")
    @SecurityRequirements
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        return ResponseEntity.ok(authService.login(request, clientIpResolver.resolve(http)));
    }

    @GetMapping("/me")
    @Operation(summary = "Devolve o usuário do token atual (valida o token)")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        return ResponseEntity.ok(new MeResponse(authentication.getName()));
    }

    @PutMapping("/password")
    @ApiResponse(responseCode = "422", description = "Senha atual incorreta ou igual à antiga")
    @Operation(summary = "Altera a senha do administrador e invalida tokens anteriores")
    public ResponseEntity<Void> changePassword(Authentication authentication,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }
}