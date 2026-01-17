package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.CompanyDto;
import com.jpriva.orders.domain.exceptions.CompanyErrorCodes;
import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.model.Company;
import com.jpriva.orders.domain.ports.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManageCompanyUseCase {

    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyDto.Response createCompany(CompanyDto.CreateRequest request) {
        Optional<Company> companyDb = companyRepository.findByTaxId(request.taxId());
        if (companyDb.isPresent()) {
            throw new DomainException(CompanyErrorCodes.COMPANY_ALREADY_EXISTS);
        }
        Company company = request.toDomain();
        Company savedCompany = companyRepository.save(company);
        return CompanyDto.Response.fromDomain(savedCompany);
    }

    @Transactional(readOnly = true)
    public CompanyDto.Response getCompany(String taxId) {
        return companyRepository.findByTaxId(taxId)
                .map(CompanyDto.Response::fromDomain)
                .orElseThrow(()->new DomainException(CompanyErrorCodes.COMPANY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<CompanyDto.Response> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(CompanyDto.Response::fromDomain)
                .toList();
    }
}
