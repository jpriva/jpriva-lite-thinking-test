package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.ClientDto;
import com.jpriva.orders.domain.exceptions.CompanyErrorCodes;
import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.model.Client;
import com.jpriva.orders.domain.model.Company;
import com.jpriva.orders.domain.ports.repository.ClientRepository;
import com.jpriva.orders.domain.ports.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManageClientUseCase {

    private final ClientRepository clientRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public ClientDto.Response createClient(ClientDto.CreateRequest request) {
        Company company = companyRepository.findByTaxId(request.companyId())
                .orElseThrow(()->new DomainException(CompanyErrorCodes.COMPANY_NOT_FOUND));
        Client client = Client.create(company.getId(), request.name(), request.email(), request.phone(), request.address());
        Client savedClient = clientRepository.save(client);
        return ClientDto.Response.fromDomain(savedClient);
    }

    @Transactional(readOnly = true)
    public Optional<ClientDto.Response> getClient(UUID id) {
        return clientRepository.findById(id).map(ClientDto.Response::fromDomain);
    }

    @Transactional(readOnly = true)
    public List<ClientDto.Response> getClientsByCompany(String taxId) {
        Company company = companyRepository.findByTaxId(taxId)
                .orElseThrow(() -> new DomainException(CompanyErrorCodes.COMPANY_NOT_FOUND));
        return clientRepository.findByCompanyId(company.getId())
                .stream().map(ClientDto.Response::fromDomain).toList();
    }
}
