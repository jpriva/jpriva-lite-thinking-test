package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.CategoryDto;
import com.jpriva.orders.domain.exceptions.CompanyErrorCodes;
import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.model.Category;
import com.jpriva.orders.domain.model.Company;
import com.jpriva.orders.domain.ports.repository.CategoryRepository;
import com.jpriva.orders.domain.ports.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public CategoryDto.Response createCategory(CategoryDto.CreateRequest request) {
        Company company = companyRepository.findByTaxId(request.companyId())
                .orElseThrow(()->new DomainException(CompanyErrorCodes.COMPANY_NOT_FOUND));
        Category category = Category.create(company.getId(), request.name(), request.description());
        Category savedCategory = categoryRepository.save(category);
        return CategoryDto.Response.fromDomain(savedCategory);
    }
    
    @Transactional(readOnly = true)
    public List<CategoryDto.Response> getCategoriesByCompany(String taxId) {
        log.info("Getting categories for company {}", taxId);
        Company company = companyRepository.findByTaxId(taxId)
                .orElseThrow(()->new DomainException(CompanyErrorCodes.COMPANY_NOT_FOUND));
        log.info("Found company {}", company);
        return categoryRepository.findByCompanyId(company.getId()).stream()
                .map(CategoryDto.Response::fromDomain)
                .collect(Collectors.toList());
    }
}
