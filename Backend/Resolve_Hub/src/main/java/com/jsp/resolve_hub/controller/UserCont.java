package com.jsp.resolve_hub.controller;

import com.jsp.resolve_hub.dto.UserResponse;
import com.jsp.resolve_hub.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Returns the profile of whoever is logged in. */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserCont {

    private final AuthService userService;

    /**
     * GET /api/auth/me. "authentication" is filled by JwtFilter from the token,
     * so getName() is the logged-in user's email. Returns a DTO, never the entity.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        return ResponseEntity.ok(new UserResponse(userService.loadUserByEmail(authentication.getName())));
    }
}