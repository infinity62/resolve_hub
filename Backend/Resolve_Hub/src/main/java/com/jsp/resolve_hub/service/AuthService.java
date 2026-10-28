package com.jsp.resolve_hub.service;

import com.jsp.resolve_hub.dao.UserRepository;
import com.jsp.resolve_hub.dto.AuthResponse;
import com.jsp.resolve_hub.dto.LoginRequest;
import com.jsp.resolve_hub.dto.RegisterRequest;
import com.jsp.resolve_hub.dto.RegisterResponse;
import com.jsp.resolve_hub.entity.RoleName;
import com.jsp.resolve_hub.entity.User;
import com.jsp.resolve_hub.exception.DuplicateEmailException;
import com.jsp.resolve_hub.exception.UserNotFoundException;
import com.jsp.resolve_hub.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Handles registration, login and loading the current user. */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Creates a new account. Everyone who registers becomes an EMPLOYEE:
     * a user must never be able to choose their own role.
     */
    @Transactional
    public RegisterResponse register(@NonNull RegisterRequest registerRequest) {

        // Normalise the email so "A@x.com" and "a@x.com" are the same account.
        String email = registerRequest.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email already registered: " + email);
        }

        User user = new User();
        user.setName(registerRequest.getName().trim());
        user.setEmail(email);
        user.setPhone(registerRequest.getPhone());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // hash, never plain text
        user.setRoles(RoleName.EMPLOYEE);
        user.setAccount_status(true);

        return new RegisterResponse(userRepository.save(user));
    }

    /**
     * Verifies the credentials and returns a JWT.
     * authenticate() throws BadCredentialsException (wrong email/password) or
     * DisabledException (deactivated), which GlobalExceptionHandler turns into 401.
     */
    public AuthResponse login(@NonNull LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));

        User user = (User) authentication.getPrincipal();

        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .expiresIn(String.valueOf(jwtService.getExpiryTime() / 1000))   // seconds
                .build();
    }

    /** Used by GET /api/auth/me to fetch the logged-in user. */
    public User loadUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}