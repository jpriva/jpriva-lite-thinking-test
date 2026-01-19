package com.jpriva.orders.application.dto;

import com.jpriva.orders.domain.model.Product;

import java.util.List;

public record InventoryData(String originalName, String sanitizedName, List<Product> products) {}