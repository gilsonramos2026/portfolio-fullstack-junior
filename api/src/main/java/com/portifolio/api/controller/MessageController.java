package com.portifolio.api.controller;

import com.portifolio.api.dto.request.MessageRequest;
import com.portifolio.api.dto.response.MessageResponse;
import com.portifolio.api.dto.response.UnreadCountResponse;
import com.portifolio.api.security.ClientIpResolver;
import com.portifolio.api.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
@Tag(
        name = "Messages",
        description = "Recados de visitantes (envio público; leitura e gestão exigem JWT de admin)"
)
public class MessageController {

    private final MessageService service;
    private final ClientIpResolver clientIpResolver;

    @PostMapping
    @SecurityRequirements
    @ApiResponse(responseCode = "201", description = "Recado registrado com sucesso")
    @ApiResponse(responseCode = "429", description = "Limite de 5 recados por hora por IP atingido")
    @Operation(summary = "Envia recado (público, com limite de 5 por hora por IP)")
    public ResponseEntity<Void> send(@Valid @RequestBody MessageRequest request, HttpServletRequest http) {
        service.receive(request, clientIpResolver.resolve(http));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @Operation(summary = "Lista os recados, do mais recente para o mais antigo")
    public ResponseEntity<List<MessageResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Quantidade de recados não lidos")
    public ResponseEntity<UnreadCountResponse> unreadCount() {
        return ResponseEntity.ok(new UnreadCountResponse(service.countUnread()));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Marca um recado como lido")
    public ResponseEntity<MessageResponse> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(service.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um recado")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}