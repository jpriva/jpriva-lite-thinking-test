package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.application.dto.CompanyDto;
import com.jpriva.orders.application.usecase.ManageCompanyUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final ManageCompanyUseCase manageCompanyUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CompanyDto.Response> createCompany(@RequestBody @Valid CompanyDto.CreateRequest request) {
        CompanyDto.Response response = manageCompanyUseCase.createCompany(request);
        return ResponseEntity.created(URI.create("/api/companies/" + response.id())).body(response);
    }

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CompanyDto.Response>> getAllCompanies() {
        return ResponseEntity.ok(manageCompanyUseCase.getAllCompanies());
    }

    @GetMapping("/{taxId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CompanyDto.Response> getCompany(@PathVariable String taxId) {
        return ResponseEntity.ok(manageCompanyUseCase.getCompany(taxId));
    }
}
