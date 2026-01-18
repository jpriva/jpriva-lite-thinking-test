package com.jpriva.orders.domain.model;

import com.jpriva.orders.domain.exceptions.ClientErrorCodes;
import com.jpriva.orders.domain.exceptions.DomainException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    void shouldCreateClientSuccessfully() {
        UUID companyId = UUID.randomUUID();
        String name = "John Client";
        String email = "john@client.com";

        Client client = Client.create(companyId, name, email, "555-1234", "St 1");

        assertNotNull(client);
        assertNotNull(client.getId());
        assertEquals(companyId, client.getCompanyId());
        assertEquals(name, client.getName());
        assertEquals(email, client.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenCreatingClientWithNullCompanyId() {
        DomainException exception = assertThrows(DomainException.class, () -> Client.builder()
                .id(UUID.randomUUID())
                .name("Name")
                .build()
        );
        assertEquals(ClientErrorCodes.CLIENT_COMPANY_ID_NULL.getCode(), exception.getCode());
    }



    @Test
    void shouldUpdateClientDetails() {
        Client client = Client.create(UUID.randomUUID(), "Old Name", "old@mail.com", "1", "1");

        client.changeName("New Name");
        client.changeEmail("new@mail.com");
        client.changePhone("999");
        client.changeAddress("New Address");

        assertEquals("New Name", client.getName());
        assertEquals("new@mail.com", client.getEmail());
        assertEquals("999", client.getPhone());
        assertEquals("New Address", client.getAddress());
    }
}
