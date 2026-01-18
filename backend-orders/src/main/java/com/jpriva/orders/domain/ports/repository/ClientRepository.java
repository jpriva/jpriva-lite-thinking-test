package com.jpriva.orders.domain.ports.repository;

import com.jpriva.orders.domain.model.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findById(UUID id);
    List<Client> findByCompanyId(UUID companyId);
    List<Client> findAll();
    void deleteById(UUID id);
}
