package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.application.dto.UserDto;
import com.jpriva.orders.application.usecase.ManageUserUseCase;
import com.jpriva.orders.infrastructure.rest.openapi.response.ProblemDetailDoc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication API")
public class AuthController {

    private final ManageUserUseCase manageUserUseCase;

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = UserDto.Response.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<UserDto.Response> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User registration details")
            @RequestBody @Valid UserDto.CreateRequest request
    ) {
        UserDto.Response response = manageUserUseCase.registerUser(request);
        return ResponseEntity.created(URI.create("/api/users/" + response.id())).body(response);
    }

    @Operation(summary = "Login a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(schema = @Schema(implementation = UserDto.TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = ProblemDetailDoc.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<UserDto.TokenResponse> loginUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User login details")
            @RequestBody @Valid UserDto.LoginRequest request
    ) {
        UserDto.TokenResponse response = manageUserUseCase.loginUser(request);
        return ResponseEntity.ok(response);
    }
}
