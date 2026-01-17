package com.jpriva.orders.domain.ports.repository;

import com.jpriva.orders.domain.model.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findById(UUID id);
    Optional<Client> findByCompanyIdAndUserId(UUID companyId, UUID userId);
    List<Client> findByCompanyId(UUID companyId);
    Optional<Client> findByUserId(UUID userId);
    List<Client> findAll();
    void deleteById(UUID id);
}
