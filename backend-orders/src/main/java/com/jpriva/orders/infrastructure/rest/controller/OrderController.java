package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.application.dto.OrderDto;
import com.jpriva.orders.application.usecase.ManageOrderUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final ManageOrderUseCase manageOrderUseCase;

    @GetMapping("/{taxId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<OrderDto.Response>> getOrders(
            @AuthenticationPrincipal UserDetails details,
            @PathVariable String taxId,
            @PageableDefault(page = 0, size = 10, sort = "orderDate") Pageable pageable
    ) {
        return ResponseEntity.ok(manageOrderUseCase.getOrders(pageable, taxId));
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrderDto.Response> createOrder(@RequestBody @Valid OrderDto.CreateRequest request) {
        log.debug("Creating order {}", request);
        OrderDto.Response response = manageOrderUseCase.createOrder(request);
        return ResponseEntity.created(URI.create("/api/orders/" + response.id())).body(response);
    }

    @GetMapping("order/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDto.Response> getOrder(@PathVariable UUID id, @AuthenticationPrincipal UserDetails details) {
        return ResponseEntity.ok(manageOrderUseCase.getOrder(id, details.getUsername()));
    }

    @PostMapping("/{id}/items")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrderDto.Response> addItem(
            @PathVariable UUID id,
            @RequestBody @Valid OrderDto.AddItemRequest request,
            @AuthenticationPrincipal UserDetails details
    ) {
        OrderDto.Response response = manageOrderUseCase.addItem(id, request, details.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrderDto.Response> removeItem(
            @PathVariable UUID id,
            @PathVariable UUID itemId,
            @AuthenticationPrincipal UserDetails details
    ) {
        OrderDto.Response response = manageOrderUseCase.removeItem(id, itemId, details.getUsername());
        return ResponseEntity.ok(response);
    }
}
