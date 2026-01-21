package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.application.dto.ProductDto;
import com.jpriva.orders.application.usecase.ManageNotificationUseCase;
import com.jpriva.orders.application.usecase.ManageProductUseCase;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management API")
public class ProductController {

    private final ManageProductUseCase manageProductUseCase;
    private final ManageNotificationUseCase manageNotificationUseCase;

    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully", content = @Content(schema = @Schema(implementation = ProductDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDto.Response> createProduct(@RequestBody @Valid ProductDto.CreateRequest request) {
        ProductDto.Response response = manageProductUseCase.createProduct(request);
        return ResponseEntity.created(URI.create("/api/products/" + response.id())).body(response);
    }

    @Operation(summary = "Get a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found", content = @Content(schema = @Schema(implementation = ProductDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDto.Response> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(manageProductUseCase.getProduct(id));
    }

    @Operation(summary = "Get all products by company tax ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products", content = @Content(schema = @Schema(implementation = ProductDto.Response.class)))
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{taxId}")
    public ResponseEntity<List<ProductDto.Response>> getProduct(@PathVariable String taxId) {
        return ResponseEntity.ok(manageProductUseCase.getAllProduct(taxId));
    }

    @Operation(summary = "Update a product's price")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price updated successfully", content = @Content(schema = @Schema(implementation = ProductDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}/price")
    public ResponseEntity<ProductDto.Response> updatePrice(
            @PathVariable UUID id,
            @RequestBody @Valid ProductDto.UpdatePriceRequest request) {
        ProductDto.Response response = manageProductUseCase.updatePrice(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Increase a product's stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock increased successfully", content = @Content(schema = @Schema(implementation = ProductDto.Response.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductDto.Response> increaseStock(
            @PathVariable UUID id,
            @RequestParam int amount) {
        ProductDto.Response response = manageProductUseCase.increaseStock(id, amount);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a PDF report of products by company tax ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF report generated successfully", content = @Content(mediaType = "application/pdf")),
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{taxId}/pdf")
    public ResponseEntity<byte[]> getPdfFile(@PathVariable String taxId) {
        byte[] pdfReport = manageProductUseCase.getPdfFile(taxId);
        return ResponseEntity.ok(pdfReport);
    }

    @Operation(summary = "Send inventory report to email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email sent successfully"),
            @ApiResponse(responseCode = "404", description = "Company not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{taxId}/email")
    public ResponseEntity<Void> sendInventoryToEmail(@PathVariable String taxId, @RequestParam String email) {
        manageNotificationUseCase.sendInventoryToEmail(taxId, email);
        return ResponseEntity.ok().build();

    }
}