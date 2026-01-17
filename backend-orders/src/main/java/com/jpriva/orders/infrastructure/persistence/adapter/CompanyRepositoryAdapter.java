package com.jpriva.orders.infrastructure.persistence.adapter;

import com.jpriva.orders.domain.model.Company;
import com.jpriva.orders.domain.ports.repository.CompanyRepository;
import com.jpriva.orders.infrastructure.persistence.entity.CompanyEntity;
import com.jpriva.orders.infrastructure.persistence.mapper.CompanyMapper;
import com.jpriva.orders.infrastructure.persistence.repository.CompanyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompanyRepositoryAdapter implements CompanyRepository {

    private final CompanyJpaRepository jpaRepository;

    @Override
    public Company save(Company company) {
        CompanyEntity entity = CompanyMapper.toEntity(company);
        CompanyEntity saved = jpaRepository.save(entity);
        return CompanyMapper.toDomain(saved);
    }

    @Override
    public Optional<Company> findById(UUID id) {
        return jpaRepository.findById(id).map(CompanyMapper::toDomain);
    }

    @Override
    public Optional<Company> findByTaxId(String taxId) {
        return jpaRepository.findByTaxId(taxId).map(CompanyMapper::toDomain);
    }

    @Override
    public List<Company> findAll() {
        return jpaRepository.findAll().stream()
                .map(CompanyMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID uuid) {
        return jpaRepository.existsById(uuid);
    }
}
