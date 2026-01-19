package com.jpriva.orders.domain.ports.report;

import com.jpriva.orders.domain.model.Product;

import java.util.List;

public interface ReportGeneratorPort {
    byte[] generateProductReport(List<Product> products);
}
