package com.portifolio.api.controller;

import com.portifolio.api.dto.request.ProjectRequest;
import com.portifolio.api.dto.response.ProjectResponse;
import com.portifolio.api.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "CRUD de projetos do portifolio (escrita exige JWT de admin)")
public class ProjectController {

    private final ProjectService service;

    @GetMapping
    @SecurityRequirements
    @Operation(summary = "Lista os projetos (destaques primeiro)")
    public ResponseEntity<List<ProjectResponse>> findAll() {
        return ResponseEntity.ok((service.findAll()));
    }

    @GetMapping("/{id}")
    @SecurityRequirements
    @Operation(summary = "Busca um projeto pelo id")
    public ResponseEntity<ProjectResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cria um projeto")
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse created = service.create((request));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza o projeto")
    public ResponseEntity<ProjectResponse> update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um projeto")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
