package com.jpriva.orders.infrastructure.rest.controller;

import com.jpriva.orders.application.dto.UserDto;
import com.jpriva.orders.application.usecase.ManageUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ManageUserUseCase manageUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<UserDto.Response> registerUser(@RequestBody @Valid UserDto.CreateRequest request) {
        UserDto.Response response = manageUserUseCase.registerUser(request);
        return ResponseEntity.created(URI.create("/api/users/" + response.id())).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<UserDto.TokenResponse> loginUser(@RequestBody @Valid UserDto.LoginRequest request) {
        UserDto.TokenResponse response = manageUserUseCase.loginUser(request);
        return ResponseEntity.ok(response);
    }
}
