package com.portifolio.api.controller;

import com.portifolio.api.dto.request.EducationRequest;
import com.portifolio.api.dto.response.EducationResponse;
import com.portifolio.api.service.EducationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/education")
public class EducationController {

    private final EducationService service;

    @GetMapping
    @SecurityRequirements
    @Operation(summary = "Lista as formações , da mais recente para a antiga")
    public ResponseEntity<List<EducationResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @SecurityRequirements
    @Operation(summary = "Busca uma formação pelo id")
    public ResponseEntity<EducationResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping()
    @ApiResponse(responseCode = "422", description = "Data de treino anterior á de início")
    @Operation(summary = "Criar uma formação")
    public ResponseEntity<EducationResponse> create(@Valid @RequestBody EducationRequest request) {
        EducationResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);

    }

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "422", description = "Data de treino anterior á de início")
    @Operation(summary = "Atualiza uma formação")
    public ResponseEntity<EducationResponse> update(@PathVariable Long id,@Valid @RequestBody EducationRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a formação")
    private ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
