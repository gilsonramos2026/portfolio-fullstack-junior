package com.portifolio.api.config;

import com.portifolio.api.exception.ApiError;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Completa a documentação OpenAPI com o que é comum a todos os endpoints, para não repetir anotações:
 * <ul>
 *   <li>o schema {@code ApiError} e seu uso em todas as respostas de erro (4xx/5xx);</li>
 *   <li>401/403 nos endpoints protegidos, 400 nos que recebem corpo e 404 nos que têm {@code {id}} no caminho;</li>
 *   <li>os códigos de sucesso reais ({@code 201} nas criações, {@code 204} nas remoções), já que o springdoc
 *       documenta {@code 200} para qualquer {@code ResponseEntity}.</li>
 * </ul>
 * Respostas já declaradas com {@code @ApiResponse} nos controllers nunca são sobrescritas.
 */
@Component
public class OpenApiErrorResponsesCustomizer implements OpenApiCustomizer {

    private static final String LOGIN_PATH = "/api/auth/login";
    private static final String PASSWORD_PATH = "/api/auth/password";
    private static final String ERROR_REF = "#/components/schemas/ApiError";

    @Override
    public void customise(OpenAPI openApi) {
        registerErrorSchemas(openApi);
        if (openApi.getPaths() == null) {
            return;
        }
        openApi.getPaths().forEach((path, item) ->
                item.readOperationsMap().forEach((method, operation) -> customise(path, method, operation)));
    }

    private void customise(String path, PathItem.HttpMethod method, Operation operation) {
        ApiResponses responses = operation.getResponses() == null ? new ApiResponses() : operation.getResponses();

        fixSuccessCode(path, method, responses);

        // security == null herda a exigência global (JWT); lista vazia (@SecurityRequirements) = endpoint público.
        boolean protectedEndpoint = operation.getSecurity() == null || !operation.getSecurity().isEmpty();
        if (protectedEndpoint) {
            addIfMissing(responses, "401", "Token ausente, inválido, expirado ou revogado (por troca de senha)");
            addIfMissing(responses, "403", "Acesso negado");
        }
        if (operation.getRequestBody() != null) {
            addIfMissing(responses, "400", "Dados inválidos ou corpo malformado (lista os campos em fieldErrors)");
        }
        if (path.contains("{")) {
            addIfMissing(responses, "404", "Recurso não encontrado");
        }

        for (Map.Entry<String, ApiResponse> entry : responses.entrySet()) {
            if (isError(entry.getKey()) && (entry.getValue().getContent() == null || entry.getValue().getContent().isEmpty())) {
                entry.getValue().setContent(errorContent());
            }
        }
        operation.setResponses(sortedByCode(responses));
    }

    /** 200 → 201 nas criações (exceto login), 200 → 204 nas remoções e na troca de senha. */
    private void fixSuccessCode(String path, PathItem.HttpMethod method, ApiResponses responses) {
        String target = null;
        if (method == PathItem.HttpMethod.POST && !LOGIN_PATH.equals(path)) {
            target = "201";
        } else if (method == PathItem.HttpMethod.DELETE && !path.endsWith("/orphans")) {
            target = "204";
        } else if (method == PathItem.HttpMethod.PUT && PASSWORD_PATH.equals(path)) {
            target = "204";
        } else if (method == PathItem.HttpMethod.PATCH) {
            return;
        }
        if (target == null || !responses.containsKey("200") || responses.containsKey(target)) {
            return;
        }
        ApiResponse ok = responses.remove("200");
        if ("204".equals(target)) {
            ok.setContent(null);
            ok.setDescription("Sem conteúdo");
        } else if (ok.getDescription() == null || "OK".equals(ok.getDescription())) {
            ok.setDescription("Criado");
        }
        responses.put(target, ok);
    }

    private static void addIfMissing(ApiResponses responses, String code, String description) {
        if (!responses.containsKey(code)) {
            responses.addApiResponse(code, new ApiResponse().description(description));
        }
    }

    private static boolean isError(String code) {
        return code.startsWith("4") || code.startsWith("5");
    }

    private static Content errorContent() {
        return new Content().addMediaType("application/json",
                new MediaType().schema(new Schema<>().$ref(ERROR_REF)));
    }

    private static ApiResponses sortedByCode(ApiResponses responses) {
        List<String> codes = new ArrayList<>(responses.keySet());
        Collections.sort(codes);
        ApiResponses sorted = new ApiResponses();
        for (String code : codes) {
            sorted.addApiResponse(code, responses.get(code));
        }
        sorted.setDefault(responses.getDefault());
        sorted.setExtensions(responses.getExtensions());
        return sorted;
    }

    private static void registerErrorSchemas(OpenAPI openApi) {
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(ApiError.class);
        if (openApi.getComponents() == null) {
            openApi.setComponents(new io.swagger.v3.oas.models.Components());
        }
        schemas.forEach((name, schema) -> openApi.getComponents().addSchemas(name, schema));
    }
}

