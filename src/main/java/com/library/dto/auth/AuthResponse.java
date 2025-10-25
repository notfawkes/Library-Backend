package com.library.dto.auth;

public record AuthResponse(
        Long id,
        String username,
        String email,
        String role,
        String token
) {}