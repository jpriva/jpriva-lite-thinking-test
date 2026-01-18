package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.application.dto.ProductDto;
import com.jpriva.orders.application.usecase.ManageProductUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ManageProductUseCase manageProductUseCase;

    @PostMapping
    public ResponseEntity<ProductDto.Response> createProduct(@RequestBody @Valid ProductDto.CreateRequest request) {
        ProductDto.Response response = manageProductUseCase.createProduct(request);
        return ResponseEntity.created(URI.create("/api/products/" + response.id())).body(response);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDto.Response> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(manageProductUseCase.getProduct(id));
    }

    @GetMapping("/{taxId}")
    public ResponseEntity<List<ProductDto.Response>> getProduct(@PathVariable String taxId) {
        return ResponseEntity.ok(manageProductUseCase.getAllProduct(taxId));
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<ProductDto.Response> updatePrice(
            @PathVariable UUID id,
            @RequestBody @Valid ProductDto.UpdatePriceRequest request) {
        ProductDto.Response response = manageProductUseCase.updatePrice(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductDto.Response> increaseStock(
            @PathVariable UUID id,
            @RequestParam int amount) {
        ProductDto.Response response = manageProductUseCase.increaseStock(id, amount);
        return ResponseEntity.ok(response);
    }
}
