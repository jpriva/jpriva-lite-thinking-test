package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.CategoryDto;
import com.jpriva.orders.domain.exceptions.CompanyErrorCodes;
import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.model.Category;
import com.jpriva.orders.domain.ports.repository.CategoryRepository;
import com.jpriva.orders.domain.ports.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public CategoryDto.Response createCategory(CategoryDto.CreateRequest request) {
        if (!companyRepository.existsById(request.companyId())){
            throw new DomainException(CompanyErrorCodes.COMPANY_NOT_FOUND);
        }
        Category category = request.toDomain();
        Category savedCategory = categoryRepository.save(category);
        return CategoryDto.Response.fromDomain(savedCategory);
    }
    
    @Transactional(readOnly = true)
    public List<CategoryDto.Response> getCategoriesByCompany(UUID companyId) {
        return categoryRepository.findByCompanyId(companyId).stream()
                .map(CategoryDto.Response::fromDomain)
                .collect(Collectors.toList());
    }
}
