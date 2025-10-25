package com.library.dto.auth;

// For registration response
public record UserDto(
        Long id,
        String username,
        String email,
        String role
) {}