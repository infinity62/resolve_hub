package com.jsp.resolve_hub.controller;

import com.jsp.resolve_hub.dto.AuthResponse;
import com.jsp.resolve_hub.dto.LoginRequest;
import com.jsp.resolve_hub.dto.RegisterRequest;
import com.jsp.resolve_hub.dto.RegisterResponse;
import com.jsp.resolve_hub.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** Public authentication endpoints (register/login) plus logout. */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService userService;

    /** POST /api/auth/register -> 201 Created. @Valid runs the annotations on RegisterRequest. */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    /** POST /api/auth/login -> 200 with { token, expiresIn }. */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    /** POST /api/auth/logout. JWT is stateless, so the client simply deletes its token. */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}