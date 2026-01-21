package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.application.dto.ClientDto;
import com.jpriva.orders.application.usecase.ManageClientUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Client management API")
public class ClientController {

    private final ManageClientUseCase manageClientUseCase;

    @Operation(summary = "Create a new client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client created successfully", content = @Content(schema = @Schema(implementation = ClientDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ClientDto.Response> createClient(@RequestBody @Valid ClientDto.CreateRequest request) {
        ClientDto.Response response = manageClientUseCase.createClient(request);
        return ResponseEntity.created(URI.create("/api/clients/" + response.id())).body(response);
    }

    @Operation(summary = "Get a client by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client found", content = @Content(schema = @Schema(implementation = ClientDto.Response.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/client/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ClientDto.Response> getClient(@PathVariable UUID id) {
        return manageClientUseCase.getClient(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get clients by company tax ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of clients", content = @Content(schema = @Schema(implementation = ClientDto.Response.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{taxId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ClientDto.Response>> getClientsByCompany(@PathVariable String taxId) {
        return ResponseEntity.ok(manageClientUseCase.getClientsByCompany(taxId).stream().toList());
    }
}