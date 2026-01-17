package com.jpriva.orders.domain.ports.security;

import com.jpriva.orders.domain.model.User;

import java.util.Map;

public interface JwtServicePort {
    String extractEmail(String token);

    String generateToken(User user);

    String generateToken(Map<String, Object> extraClaims, User user);

    boolean isTokenValid(String token, User user);
}
