package com.jsp.resolve_hub.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * The security rulebook of the application.
 * @EnableMethodSecurity turns on @PreAuthorize used in the controllers.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AppUserDetailsService userDetailsService;
    private final JwtService jwtService;

    // Comma-separated list from application.properties.
    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    /** Defines which URLs are public, which need a role, and how errors look. */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .cors(Customizer.withDefaults())            // uses corsConfigurationSource() below
                .csrf(csrf -> csrf.disable())               // not needed: no cookies/sessions, we use JWT
                .authorizeHttpRequests(auth -> auth
                        // Only these two endpoints are public.
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        // Swagger documentation pages.
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Everything else (including /me and /logout) needs a valid token.
                        .anyRequest().authenticated())
                // STATELESS: the server keeps no session; every request carries its own token.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Security-layer errors happen before controllers, so GlobalExceptionHandler
                // cannot catch them. These two handlers write the same JSON shape.
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) ->
                                writeError(request, response, HttpStatus.UNAUTHORIZED,
                                        "Authentication is required or the token is invalid", "UNAUTHORIZED"))
                        .accessDeniedHandler((request, response, e) ->
                                writeError(request, response, HttpStatus.FORBIDDEN,
                                        "You do not have permission to access this resource", "ACCESS_DENIED")))
                // Run our JWT filter before Spring's default login filter.
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /** Lets the React app (a different origin/port) call this API from the browser. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /** BCrypt hashes passwords on register and checks them on login. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** The component AuthService calls to verify email + password. */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationProvider) throws Exception {
        return authenticationProvider.getAuthenticationManager();
    }

    // Intentionally NOT a @Bean: a Filter bean would also be auto-registered
    // as a servlet filter outside the security chain (it would run twice).
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtService, userDetailsService);
    }

    /** Writes the standard error JSON for 401/403 raised inside the security filters. */
    private static void writeError(HttpServletRequest request, HttpServletResponse response,
                                   HttpStatus status, String message, String code) throws IOException {
        String path = request.getRequestURI().replace("\\", "\\\\").replace("\"", "\\\"");
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\",\"code\":\"%s\"}",
                LocalDateTime.now(), status.value(), status.name(), message, path, code));
    }
}