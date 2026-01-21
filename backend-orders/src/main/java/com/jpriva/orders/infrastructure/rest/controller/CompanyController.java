package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.application.dto.CompanyDto;
import com.jpriva.orders.application.usecase.ManageCompanyUseCase;
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

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "Company management API")
public class CompanyController {

    private final ManageCompanyUseCase manageCompanyUseCase;

    @Operation(summary = "Create a new company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Company created successfully", content = @Content(schema = @Schema(implementation = CompanyDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CompanyDto.Response> createCompany(@RequestBody @Valid CompanyDto.CreateRequest request) {
        CompanyDto.Response response = manageCompanyUseCase.createCompany(request);
        return ResponseEntity.created(URI.create("/api/companies/" + response.id())).body(response);
    }

    @Operation(summary = "Get all companies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all companies", content = @Content(schema = @Schema(implementation = CompanyDto.Response.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CompanyDto.Response>> getAllCompanies() {
        return ResponseEntity.ok(manageCompanyUseCase.getAllCompanies());
    }

    @Operation(summary = "Get a company by tax ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company found", content = @Content(schema = @Schema(implementation = CompanyDto.Response.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Company not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{taxId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CompanyDto.Response> getCompany(@PathVariable String taxId) {
        return ResponseEntity.ok(manageCompanyUseCase.getCompany(taxId));
    }
}
