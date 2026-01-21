package com.jpriva.orders.application.dto;

import com.jpriva.orders.domain.model.User;
import com.jpriva.orders.domain.model.vo.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto() {

    @Schema(description = "Request to create a new user")
    public record CreateRequest(
            @Schema(description = "User's email address", example = "test.user@mail.com")
            @NotBlank(message = "Email is required")
            @Email(message = "Invalid email format")
            String email,
            @Schema(description = "User's password", example = "Password123!")
            @NotBlank(message = "Password is required")
            String password,
            @Schema(description = "User's full name", example = "Test User")
            @NotBlank(message = "Full Name is required")
            String fullName,
            @Schema(description = "User's phone number", example = "+1234567890")
            String phone,
            @Schema(description = "User's address", example = "123 Main St, Anytown, USA")
            String address,
            @Schema(description = "User's role", example = "EXTERNAL", allowableValues = {"EXTERNAL", "ADMIN"})
            String role
    ) {
        public User toDomain(String passwordHashed) {
            return User.create(email, passwordHashed, fullName, phone, address, Role.valueOf(role));
        }
    }

    @Schema(description = "Request to login a user")
    public record LoginRequest(
            @Schema(description = "User's email address", example = "test.user@mail.com")
            @NotBlank(message = "Email is required")
            String email,
            @Schema(description = "User's password", example = "Password123!")
            @NotBlank(message = "Password is required")
            String password
    ) {
    }

    @Schema(description = "Response containing JWT token")
    public record TokenResponse(
            @Schema(description = "Access Token (JWT)")
            String accessToken,
            @Schema(description = "Token type", example = "Bearer")
            String tokenType,
            @Schema(description = "Token expiration time in seconds", example = "3600")
            Long expiresIn,
            @Schema(description = "Token issuance time in milliseconds", example = "1678886400000")
            Long issuedAt
    ){}

    @Schema(description = "Response containing user details")
    public record Response(
            @Schema(description = "User's unique identifier")
            UUID id,
            @Schema(description = "User's email address", example = "test.user@mail.com")
            String email,
            @Schema(description = "User's full name", example = "Test User")
            String fullName,
            @Schema(description = "User's phone number", example = "+1234567890")
            String phone,
            @Schema(description = "User's address", example = "123 Main St, Anytown, USA")
            String address,
            @Schema(description = "User's role", example = "EXTERNAL")
            String role,
            @Schema(description = "Timestamp of user creation")
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
