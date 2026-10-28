package com.jsp.resolve_hub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Database entity for the "userss" table.
 *
 * It also implements Spring Security's UserDetails, so the same object is used
 * for database access AND for authentication. Spring Security reads the
 * username, password, authorities and enabled flag from the methods below.
 */
@Entity
@Table(name = "userss")
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL auto-increment
    private int id;

    private String name;

    // unique + not null: the database itself blocks duplicate emails,
    // even if two registrations arrive at the same instant.
    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    // Always a BCrypt hash, never plain text.
    private String password;

    // Stored as text ("ADMIN"), not as a number, so the data stays readable.
    @Enumerated(EnumType.STRING)
    private RoleName roles;

    // true = active, false = deactivated by an admin.
    private boolean account_status;

    @CreationTimestamp
    private LocalDateTime created_at;   // set automatically on insert

    @UpdateTimestamp
    private LocalDateTime updated_at;   // refreshed automatically on every update

    /** Spring Security expects roles in the form "ROLE_ADMIN". */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) return Collections.emptySet();
        return List.of(new SimpleGrantedAuthority("ROLE_" + roles.name()));
    }

    /** The login identifier is the email. */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** Deactivated users (account_status = false) cannot log in. */
    @Override
    public boolean isEnabled() {
        return account_status;
    }
}