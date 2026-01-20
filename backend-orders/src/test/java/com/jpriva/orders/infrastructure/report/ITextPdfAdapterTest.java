package com.jpriva.orders.infrastructure.report;

import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.ReportErrorCodes;
import com.jpriva.orders.domain.model.Product;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ITextPdfAdapterTest {

    private final ITextPdfAdapter adapter = new ITextPdfAdapter();

    @Test
    void shouldGeneratePdfReportWithoutErrors() {
        UUID companyId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Product product1 = Product.create(companyId, categoryId, "Product 1", "SKU123", "Description 1");
        product1.increaseStock(10);

        Product product2 = Product.create(companyId, categoryId, "Product 2", "SKU456", "Description 2");
        product2.increaseStock(20);
        List<Product> products = List.of(product1, product2);

        byte[] pdfBytes = adapter.generateProductReport(products);

        assertThat(pdfBytes).isNotNull();
        assertThat(pdfBytes.length).isGreaterThan(0);

        String header = new String(pdfBytes, 0, 4);
        assertThat(header).isEqualTo("%PDF");
    }

    @Test
    void shouldThrowDomainExceptionWhenInputListIsNull() {
        assertThatThrownBy(() -> adapter.generateProductReport(null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(ReportErrorCodes.REPORT_GENERATE_ERROR.getMessage());
    }
}
