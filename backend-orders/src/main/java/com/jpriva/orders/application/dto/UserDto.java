package com.jpriva.orders.application.dto;

import com.jpriva.orders.domain.model.User;
import com.jpriva.orders.domain.model.vo.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto() {

    public record CreateRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Invalid email format")
            String email,
            @NotBlank(message = "Password is required")
            String password,
            @NotBlank(message = "Full Name is required")
            String fullName,
            String phone,
            String address,
            String role
    ) {
        public User toDomain(String passwordHashed) {
            return User.create(email, passwordHashed, fullName, phone, address, Role.valueOf(role));
        }
    }

    public record LoginRequest(
            @NotBlank(message = "Email is required")
            String email,
            @NotBlank(message = "Password is required")
            String password
    ) {
    }

    public record TokenResponse(
            String accessToken,
            String tokenType,
            Long expiresIn,
            Long issuedAt
    ){}

    public record Response(
            UUID id,
            String email,
            String fullName,
            String phone,
            String address,
            String role,
            LocalDateTime createdAt
    ) {
        public static Response fromDomain(User user) {
            return new Response(
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getPhone(),
                    user.getAddress(),
                    user.getRole().name(),
                    user.getCreatedAt()
            );
        }
    }
}
