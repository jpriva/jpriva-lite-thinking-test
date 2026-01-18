package com.jpriva.orders.application.usecase;

import com.jpriva.orders.application.dto.UserDto;
import com.jpriva.orders.domain.exceptions.DomainException;
import com.jpriva.orders.domain.exceptions.UserErrorCodes;
import com.jpriva.orders.domain.model.User;
import com.jpriva.orders.domain.ports.repository.UserRepository;
import com.jpriva.orders.domain.ports.security.JwtServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManageUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServicePort jwtService;

    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    @Transactional
    public UserDto.Response registerUser(UserDto.CreateRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("User with email " + request.email() + " already exists");
        }
        String password = request.password();
        String passwordHashed = passwordEncoder.encode(password);
        User user = request.toDomain(passwordHashed);
        User savedUser = userRepository.save(user);
        return UserDto.Response.fromDomain(savedUser);
    }
    
    @Transactional(readOnly = true)
    public Optional<UserDto.Response> getUser(UUID id) {
        return userRepository.findById(id).map(UserDto.Response::fromDomain);
    }

    @Transactional(readOnly = true)
    public Optional<UserDto.Response> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(UserDto.Response::fromDomain);
    }

    public UserDto.TokenResponse loginUser(UserDto.LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new DomainException(UserErrorCodes.USER_CREDENTIALS_INVALID));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new DomainException(UserErrorCodes.USER_CREDENTIALS_INVALID);
        }

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", user.getRole().name());
        String token = jwtService.generateToken(extraClaims, user);
        return new UserDto.TokenResponse(token, "Bearer", jwtExpiration, Instant.now().toEpochMilli());
    }
}
