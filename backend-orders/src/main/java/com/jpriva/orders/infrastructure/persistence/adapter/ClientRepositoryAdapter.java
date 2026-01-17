package com.jpriva.orders.infrastructure.persistence.adapter;

import com.jpriva.orders.domain.model.Client;
import com.jpriva.orders.domain.ports.repository.ClientRepository;
import com.jpriva.orders.infrastructure.persistence.entity.ClientEntity;
import com.jpriva.orders.infrastructure.persistence.mapper.ClientMapper;
import com.jpriva.orders.infrastructure.persistence.repository.ClientJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClientRepositoryAdapter implements ClientRepository {

    private final ClientJpaRepository jpaRepository;

    @Override
    public Client save(Client client) {
        ClientEntity entity = ClientMapper.toEntity(client);
        ClientEntity saved = jpaRepository.save(entity);
        return ClientMapper.toDomain(saved);
    }

    @Override
    public Optional<Client> findById(UUID id) {
        return jpaRepository.findById(id).map(ClientMapper::toDomain);
    }

    @Override
    public Optional<Client> findByCompanyIdAndUserId(UUID companyId, UUID userId) {
        return jpaRepository.findByCompanyIdAndUserId(companyId, userId).map(ClientMapper::toDomain);
    }

    @Override
    public List<Client> findByCompanyId(UUID companyId) {
        return jpaRepository.findByCompanyId(companyId).stream()
                .map(ClientMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Client> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(ClientMapper::toDomain);
    }

    @Override
    public List<Client> findAll() {
        return jpaRepository.findAll().stream()
                .map(ClientMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
