package com.jpriva.orders.domain.ports.repository;

import com.jpriva.orders.domain.model.Company;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {
    Company save(Company company);
    Optional<Company> findById(UUID id);
    Optional<Company> findByTaxId(String taxId);
    List<Company> findAll();
    void deleteById(UUID id);

    boolean existsById(UUID uuid);
}
