package com.portifolio.api.controller;

import com.portifolio.api.dto.request.ProfileRequest;
import com.portifolio.api.dto.request.ProjectRequest;
import com.portifolio.api.dto.response.ProfileResponse;
import com.portifolio.api.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
@Tag(name = "Profile", description = "Perfil exibido no portifólio (escrita exige JWT de admin")
public class ProfileController {

    private final ProfileService service;

    @GetMapping
    @SecurityRequirements
    @Operation(summary = "Devolve o perfil público")
    public ResponseEntity<ProfileResponse> get() {
        return ResponseEntity.ok(service.get());
    }

    @PutMapping
    @Operation(summary = "Atualiza o perfil")
    public ResponseEntity<ProfileResponse> update(@Valid @RequestBody ProfileRequest request) {
        return ResponseEntity.ok(service.update(request));
    }
}
