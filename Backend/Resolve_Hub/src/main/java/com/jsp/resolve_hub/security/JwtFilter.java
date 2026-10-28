package com.jsp.resolve_hub.security;

import com.jsp.resolve_hub.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Runs once on EVERY request, before the controller.
 *
 *   1. Read the "Authorization: Bearer <token>" header.
 *   2. Validate the token (signature + expiry).
 *   3. Load the user from the database.
 *   4. If the user is active, store them in the SecurityContext.
 *
 * It never rejects a request itself. If anything is wrong the request simply
 * stays "unauthenticated", and SecurityConfig then answers 401 or 403.
 */
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AppUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // No token, wrong prefix, or already authenticated: nothing to do.
        if (header == null || !header.startsWith("Bearer ")
                || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);   // remove "Bearer "

        if (jwtService.validateToken(token)) {
            try {
                String username = jwtService.extractUserName(token);
                User user = (User) userDetailsService.loadUserByUsername(username);

                // Deactivated users are rejected even if their token is still valid.
                if (user.isEnabled()) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (UsernameNotFoundException ignored) {
                // user was deleted after the token was issued -> stay unauthenticated
            }
        }

        filterChain.doFilter(request, response);
    }
}