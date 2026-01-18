package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.ClientDto;
import com.jpriva.orders.domain.model.Client;
import com.jpriva.orders.domain.ports.repository.ClientRepository;
import com.jpriva.orders.domain.ports.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jpriva.orders.domain.model.Company;

@ExtendWith(MockitoExtension.class)
class ManageClientUseCaseTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private ManageClientUseCase manageClientUseCase;

    private ClientDto.CreateRequest createRequest;
    private Client client;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        testCompany = Company.builder()
                .id(UUID.randomUUID())
                .name("TestCo")
                .taxId("T1")
                .address("Comp Address")
                .phone("123")
                .build();

        createRequest = new ClientDto.CreateRequest(
                testCompany.getTaxId(),
                "Test Client",
                "client@example.com",
                "987654321",
                "Client Address"
        );

        client = Client.builder()
                .id(UUID.randomUUID())
                .companyId(testCompany.getId())
                .name(createRequest.name())
                .email(createRequest.email())
                .phone(createRequest.phone())
                .address(createRequest.address())
                .build();
    }

    @Test
    void createClient_shouldSaveClient() {
        when(companyRepository.findByTaxId(any(String.class))).thenReturn(java.util.Optional.of(testCompany));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        ClientDto.Response result = manageClientUseCase.createClient(createRequest);

        verify(clientRepository).save(any(Client.class));
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(createRequest.name());
    }

    @Test
    void getClient_shouldReturnClient_whenFound() {
        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        Optional<ClientDto.Response> result = manageClientUseCase.getClient(client.getId());

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(client.getId());
    }

    @Test
    void getClient_shouldReturnEmpty_whenNotFound() {
        when(clientRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Optional<ClientDto.Response> result = manageClientUseCase.getClient(UUID.randomUUID());

        assertThat(result).isNotPresent();
    }
}
