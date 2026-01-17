package com.jpriva.orders.domain.model;

import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.UserErrorCodes;
import com.jpriva.orders.domain.model.vo.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserSuccessfully() {
        UUID id = UUID.randomUUID();
        String email = "john.doe@example.com";
        String pass = "secret_hash";
        String fullName = "John Doe";
        String phone = "1234567890";
        String address = "123 Main St";
        Role role = Role.ADMIN;
        LocalDateTime createdAt = LocalDateTime.now();

        User user = new User(id, email, pass, fullName, phone, address, role, createdAt);

        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(fullName, user.getFullName());
        assertEquals(role, user.getRole());
    }

    @Test
    void shouldCreateUserUsingFactoryMethod() {
        String email = "jane.doe@example.com";
        String fullName = "Jane Doe";
        String pass = "secret_hash";
        Role role = Role.EXTERNAL;

        User user = User.create(email, pass, fullName, "9876543210", "456 Elm St", role);

        assertNotNull(user);
        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());
        assertEquals(email, user.getEmail());
        assertEquals(role, user.getRole());
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithNullId() {
        DomainException exception = assertThrows(DomainException.class, () -> new User(
                null,
                "email@test.com",
                "pass",
                "Name",
                "Phone",
                "Address",
                Role.ADMIN,
                LocalDateTime.now()
        ));

        assertEquals(UserErrorCodes.USER_ID_NULL.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithNullEmail() {
        DomainException exception = assertThrows(DomainException.class, () -> User.create(
                null,
                "pass",
                "Name",
                "Phone",
                "Address",
                Role.ADMIN
        ));

        assertEquals(UserErrorCodes.USER_EMAIL_NULL.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithEmptyEmail() {
        DomainException exception = assertThrows(DomainException.class, () -> User.create(
                "   ",
                "pass",
                "Name",
                "Phone",
                "Address",
                Role.ADMIN
        ));

        assertEquals(UserErrorCodes.USER_EMAIL_NULL.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithNullName() {
        DomainException exception = assertThrows(DomainException.class, () -> User.create(
                "email@test.com",
                "pass",
                null,
                "Phone",
                "Address",
                Role.ADMIN
        ));

        assertEquals(UserErrorCodes.USER_NAME_NULL.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithNullRole() {
        DomainException exception = assertThrows(DomainException.class, () -> User.create(
                "email@test.com",
                "pass",
                "Name",
                "Phone",
                "Address",
                null
        ));

        assertEquals(UserErrorCodes.USER_ROLE_NULL.getCode(), exception.getCode());
    }

    @Test
    void shouldUpdateUserDetails() {
        User user = User.create("email@test.com", "pass", "Old Name", "Old Phone", "Old Address", Role.EXTERNAL);

        user.changeFullName("New Name");
        user.changePhone("New Phone");
        user.changeAddress("New Address");

        assertEquals("New Name", user.getFullName());
        assertEquals("New Phone", user.getPhone());
        assertEquals("New Address", user.getAddress());
    }

    @Test
    void shouldAllowNullPhoneAndAddressOnUpdate() {
        User user = User.create("email@test.com", "pass", "Name", "Phone", "Address", Role.EXTERNAL);

        user.changePhone(null);
        user.changeAddress("   ");

        assertNull(user.getPhone());
        assertNull(user.getAddress());
    }
}
