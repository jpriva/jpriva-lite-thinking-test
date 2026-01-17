package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.ClientDto;
import com.jpriva.orders.domain.model.Client;
import com.jpriva.orders.domain.ports.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManageClientUseCase {

    private final ClientRepository clientRepository;

    @Transactional
    public ClientDto.Response createClient(ClientDto.CreateRequest request) {
        Client client = request.toDomain();
        Client savedClient = clientRepository.save(client);
        return ClientDto.Response.fromDomain(savedClient);
    }
    
    @Transactional(readOnly = true)
    public Optional<ClientDto.Response> getClient(UUID id) {
        return clientRepository.findById(id).map(ClientDto.Response::fromDomain);
    }
}
