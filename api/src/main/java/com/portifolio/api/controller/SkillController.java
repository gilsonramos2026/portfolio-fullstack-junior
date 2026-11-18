package com.portifolio.api.controller;

import com.portifolio.api.dto.request.SkillRequest;
import com.portifolio.api.dto.response.SkillResponse;
import com.portifolio.api.enums.SkillCategory;
import com.portifolio.api.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/skills")
@Tag(name = "Skills", description = "CRUD de habilidades (escrita exige JWT de admin)")
public class SkillController {

    private final SkillService service;

    @GetMapping
    @SecurityRequirements
    @Operation(summary = "Lista as skills, com filtro opcional por categoria")
    public ResponseEntity<List<SkillResponse>> findAll(@RequestParam(required = false)SkillCategory category) {
        return ResponseEntity.ok(category == null ? service.findAll() : service.findByCategory(category));
    }

    @GetMapping("/{id}")
    @SecurityRequirements
    @Operation(summary = "Busca uma skill pelo id")
    public ResponseEntity<SkillResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "409", description = "Já existe uma skill com esse nome")
    @Operation(summary = "Cria uma skill")
    public ResponseEntity<SkillResponse> create(@Valid @RequestBody SkillRequest request) {
        SkillResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "409", description = "Já existe uma skill com esse nome")
    @Operation(summary = "Atualiza uma skill")
    public ResponseEntity<SkillResponse> update(@PathVariable Long id, @Valid @RequestBody SkillRequest request) {
       return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma skill")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
