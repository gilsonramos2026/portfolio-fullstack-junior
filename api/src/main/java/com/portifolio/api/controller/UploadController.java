package com.portifolio.api.controller;


import com.portifolio.api.dto.response.OrphanFilesResponse;
import com.portifolio.api.dto.response.UploadResponse;
import com.portifolio.api.service.FileStorageService;
import com.portifolio.api.service.UploadMaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Uploads", description = "Imagens enviadas pelo admin (envio exige JWT; leitura é pública)")
public class UploadController {

    private final FileStorageService storage;
    private final UploadMaintenanceService maintenance;

    @PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(responseCode = "413", description = "Arquivo acima do limite do servidor")
    @ApiResponse(responseCode = "422", description = "Arquivo vazio, acima de 5 MB, formato não suportado, corrompido ou com dimensões grandes demais")
    @Operation(summary = "Envia uma imagem (PNG, JPEG, WebP ou GIF, até 5 MB) e devolve a URL pública")
    public ResponseEntity<UploadResponse> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(201).body(new UploadResponse(storage.store(file)));
    }

    @GetMapping("/uploads/orphans")
    @Operation(summary = "Lista as imagens enviadas que nenhum projeto, skill ou perfil usa (com mais de 24 h)")
    public ResponseEntity<OrphanFilesResponse> orphans() {
        return ResponseEntity.ok(maintenance.findOrphans());
    }

    @DeleteMapping("/uploads/orphans")
    @Operation(summary = "Remove as imagens órfãs listadas por GET /uploads/orphans")
    public ResponseEntity<OrphanFilesResponse> deleteOrphans() {
        return ResponseEntity.ok(maintenance.deleteOrphans());
    }

    @GetMapping("/files/{filename}")
    @SecurityRequirements
    @Operation(summary = "Serve uma imagem enviada")
    public ResponseEntity<Resource> download(@PathVariable String filename) {
        FileStorageService.StoredFile stored = storage.load(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(stored.type().mediaType()))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable())
                .header("X-Content-Type-Options", "nosniff")
                .body(stored.resource());
    }
}
