package com.aiinterviewcoach.security;

import com.aiinterviewcoach.entity.User;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public final class AuthenticatedUser implements UserDetails {
    private final Long id;
    private final String email;
    private final String passwordHash;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthenticatedUser(Long id, String email, String passwordHash, String role) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    public static AuthenticatedUser from(User user) {
        return new AuthenticatedUser(
                user.getId(), user.getEmail(), user.getPasswordHash(), user.getRole().name());
    }

    public Long getId() { return id; }
    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return passwordHash; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
}

