package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.auth.AuthResponse;
import com.library.dto.auth.LoginRequest;
import com.library.dto.auth.RegisterRequest;
import com.library.dto.auth.UserDto;
import com.library.entity.User;
import com.library.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        UserDto registeredUser = authService.register(request);
        return new ResponseEntity<>(
                ApiResponse.success(registeredUser, "User registered successfully", 201),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success(authResponse, "Login successful", 200)
        );
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<UserDto>> validateToken(
            @AuthenticationPrincipal User user // Spring Security magically injects the authenticated user
    ) {
        // If this endpoint is reached, the token is valid (thanks to JwtAuthenticationFilter)
        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(
                ApiResponse.success(userDto, "Token is valid", 200)
        );
    }
}