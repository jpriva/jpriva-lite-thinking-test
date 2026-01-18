package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.application.dto.ClientDto;
import com.jpriva.orders.application.usecase.ManageClientUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ManageClientUseCase manageClientUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ClientDto.Response> createClient(@RequestBody @Valid ClientDto.CreateRequest request) {
        ClientDto.Response response = manageClientUseCase.createClient(request);
        return ResponseEntity.created(URI.create("/api/clients/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ClientDto.Response> getClient(@PathVariable UUID id) {
        return manageClientUseCase.getClient(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}