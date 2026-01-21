package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.application.dto.OrderDto;
import com.jpriva.orders.application.usecase.ManageOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ProblemDetail;
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
@Tag(name = "Orders", description = "Order management API")
public class OrderController {

    private final ManageOrderUseCase manageOrderUseCase;

    @Operation(summary = "Get orders by company tax ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of orders", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{taxId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<OrderDto.Response>> getOrders(
            @PathVariable String taxId,
            @PageableDefault(sort = "orderDate") Pageable pageable
    ) {
        return ResponseEntity.ok(manageOrderUseCase.getOrders(pageable, taxId));
    }

    @Operation(summary = "Create a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully", content = @Content(schema = @Schema(implementation = OrderDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OrderDto.Response> createOrder(@RequestBody @Valid OrderDto.CreateRequest request) {
        log.debug("Creating order {}", request);
        OrderDto.Response response = manageOrderUseCase.createOrder(request);
        return ResponseEntity.created(URI.create("/api/orders/" + response.id())).body(response);
    }

    @Operation(summary = "Get an order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found", content = @Content(schema = @Schema(implementation = OrderDto.Response.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/order/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderDto.Response> getOrder(@PathVariable UUID id, @AuthenticationPrincipal UserDetails details) {
        return ResponseEntity.ok(manageOrderUseCase.getOrder(id, details.getUsername()));
    }

    @Operation(summary = "Add an item to an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added successfully", content = @Content(schema = @Schema(implementation = OrderDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
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

    @Operation(summary = "Remove an item from an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed successfully", content = @Content(schema = @Schema(implementation = OrderDto.Response.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Order or item not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
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
