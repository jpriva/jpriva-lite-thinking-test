package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.application.dto.CategoryDto;
import com.jpriva.orders.application.usecase.ManageCategoryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management API")
public class CategoryController {

    private final ManageCategoryUseCase manageCategoryUseCase;

    @Operation(summary = "Create a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully", content = @Content(schema = @Schema(implementation = CategoryDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CategoryDto.Response> createCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Category creation request")
            @RequestBody @Valid CategoryDto.CreateRequest request) {
        CategoryDto.Response response = manageCategoryUseCase.createCategory(request);
        return ResponseEntity.created(URI.create("/api/categories/" + response.id())).body(response);
    }

    @Operation(summary = "Get categories by company tax ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of categories", content = @Content(schema = @Schema(implementation = CategoryDto.Response.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{taxId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CategoryDto.Response>> getCategoriesByCompany(
            @Parameter(description = "Company's tax ID") @PathVariable String taxId) {
        return ResponseEntity.ok(manageCategoryUseCase.getCategoriesByCompany(taxId));
    }
}
