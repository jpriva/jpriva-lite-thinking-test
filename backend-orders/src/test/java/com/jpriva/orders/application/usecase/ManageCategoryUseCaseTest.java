package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.CategoryDto;
import com.jpriva.orders.domain.model.Category;
import com.jpriva.orders.domain.ports.repository.CategoryRepository;
import com.jpriva.orders.domain.ports.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageCategoryUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private ManageCategoryUseCase manageCategoryUseCase;

    private Category category;

    @BeforeEach
    void setUp() {
        UUID companyId = UUID.randomUUID();
        category = Category.builder()
                .id(UUID.randomUUID())
                .companyId(companyId)
                .name("Test Category")
                .description("Description")
                .build();
    }

    @Test
    void getCategoriesByCompany_shouldReturnList() {
        when(categoryRepository.findByCompanyId(any(UUID.class))).thenReturn(Collections.singletonList(category));

        List<CategoryDto.Response> result = manageCategoryUseCase.getCategoriesByCompany(category.getCompanyId());

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(category.getId());
    }
}
