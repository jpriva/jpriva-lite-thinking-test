package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.application.dto.CategoryDto;
import com.jpriva.orders.application.usecase.ManageCategoryUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ManageCategoryUseCase manageCategoryUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CategoryDto.Response> createCategory(@RequestBody @Valid CategoryDto.CreateRequest request) {
        CategoryDto.Response response = manageCategoryUseCase.createCategory(request);
        return ResponseEntity.created(URI.create("/api/categories/" + response.id())).body(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CategoryDto.Response>> getCategoriesByCompany(@RequestParam UUID companyId) {
        return ResponseEntity.ok(manageCategoryUseCase.getCategoriesByCompany(companyId));
    }
}
