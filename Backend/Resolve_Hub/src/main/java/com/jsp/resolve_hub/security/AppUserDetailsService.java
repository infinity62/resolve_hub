package com.jsp.resolve_hub.security;

import com.jsp.resolve_hub.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Tells Spring Security how to find a user by "username" (our email).
 * Used by the AuthenticationManager during login and by JwtFilter on every request.
 */
@Component
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Spring converts UsernameNotFoundException into BadCredentialsException during login,
        // so "unknown email" and "wrong password" look identical to the client.
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}