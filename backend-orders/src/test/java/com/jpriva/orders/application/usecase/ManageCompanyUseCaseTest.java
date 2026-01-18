package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.CompanyDto;
import com.jpriva.orders.domain.exceptions.CompanyErrorCodes;
import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.model.Company;
import com.jpriva.orders.domain.ports.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManageCompanyUseCaseTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private ManageCompanyUseCase manageCompanyUseCase;

    private CompanyDto.CreateRequest createRequest;
    private Company company;

    @BeforeEach
    void setUp() {
        createRequest = new CompanyDto.CreateRequest("Test Company", "T12345678", "Address", "12345");
        company = Company.builder()
                .id(java.util.UUID.randomUUID())
                .name(createRequest.name())
                .taxId(createRequest.taxId())
                .address(createRequest.address())
                .phone(createRequest.phone())
                .build();
    }

    @Test
    void createCompany_shouldSaveCompany_whenTaxIdIsNew() {
        when(companyRepository.findByTaxId(anyString())).thenReturn(Optional.empty());
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        CompanyDto.Response result = manageCompanyUseCase.createCompany(createRequest);

        verify(companyRepository).save(any(Company.class));
        assertThat(result).isNotNull();
        assertThat(result.taxId()).isEqualTo(createRequest.taxId());
    }

    @Test
    void createCompany_shouldThrowException_whenTaxIdExists() {
        when(companyRepository.findByTaxId(anyString())).thenReturn(Optional.of(company));

        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> manageCompanyUseCase.createCompany(createRequest))
                .extracting(DomainException::getCode)
                .isEqualTo(CompanyErrorCodes.COMPANY_ALREADY_EXISTS.getCode());
    }

    @Test
    void getCompany_shouldReturnCompany_whenFound() {
        when(companyRepository.findByTaxId(anyString())).thenReturn(Optional.of(company));

        CompanyDto.Response result = manageCompanyUseCase.getCompany(company.getTaxId());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(company.getId());
    }

    @Test
    void getCompany_shouldThrowException_whenNotFound() {
        when(companyRepository.findByTaxId(anyString())).thenReturn(Optional.empty());

        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> manageCompanyUseCase.getCompany("nonexistent"))
                .extracting(DomainException::getCode)
                .isEqualTo(CompanyErrorCodes.COMPANY_NOT_FOUND.getCode());
    }

    @Test
    void getAllCompanies_shouldReturnListOfCompanies() {
        when(companyRepository.findAll()).thenReturn(Collections.singletonList(company));

        List<CompanyDto.Response> result = manageCompanyUseCase.getAllCompanies();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(company.getId());
    }
}
