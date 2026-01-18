package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.CategoryDto;
import com.jpriva.orders.domain.model.Category;
import com.jpriva.orders.domain.model.Company; // Added Company import
import com.jpriva.orders.domain.ports.repository.CategoryRepository;
import com.jpriva.orders.domain.ports.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class ManageCategoryUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private ManageCategoryUseCase manageCategoryUseCase;

    private Category category;
    private Company company;

    @BeforeEach
    void setUp() {
        UUID companyId = UUID.randomUUID();
        company = Company.builder()
                .id(companyId)
                .name("Test Company")
                .taxId("123456789")
                .address("Test Address")
                .phone("1234567890")
                .createdAt(LocalDateTime.now())
                .build();

        category = Category.builder()
                .id(UUID.randomUUID())
                .companyId(companyId)
                .name("Test Category")
                .description("Description")
                .build();
    }

    @Test
    void createCategory_shouldReturnSavedCategory() {
        CategoryDto.CreateRequest createRequest = new CategoryDto.CreateRequest(
                company.getTaxId(),
                "New Category",
                "New Description"
        );
        Category newCategory = Category.create(company.getId(), createRequest.name(), createRequest.description());

        when(companyRepository.findByTaxId(any(String.class))).thenReturn(java.util.Optional.of(company));
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        CategoryDto.Response result = manageCategoryUseCase.createCategory(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(createRequest.name());
        assertThat(result.description()).isEqualTo(createRequest.description());
        assertThat(result.companyId()).isEqualTo(company.getId());

        verify(companyRepository, times(1)).findByTaxId(createRequest.companyId());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void getCategoriesByCompany_shouldReturnList() {
        when(companyRepository.findByTaxId(any(String.class))).thenReturn(java.util.Optional.of(company));
        when(categoryRepository.findByCompanyId(any(UUID.class))).thenReturn(Collections.singletonList(category));

        List<CategoryDto.Response> result = manageCategoryUseCase.getCategoriesByCompany(company.getTaxId());

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(category.getId());
    }
}