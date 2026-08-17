package com.aiinterviewcoach.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

class JwtServiceTest {
    private static final String SECRET = "unit-test-secret-that-is-at-least-thirty-two-bytes";

    @Test
    void generatesAndValidatesSignedToken() {
        JwtService service = new JwtService(SECRET, 60_000);
        AuthenticatedUser user = new AuthenticatedUser(42L, "user@example.com", "hash", "USER");

        String token = service.generateToken(user);

        assertThat(service.extractUsername(token)).isEqualTo("user@example.com");
        assertThat(service.isTokenValid(token, user)).isTrue();
        assertThat(service.getExpirationSeconds()).isEqualTo(60);
    }

    @Test
    void rejectsTamperedToken() {
        JwtService service = new JwtService(SECRET, 60_000);
        String token = service.generateToken(
                new AuthenticatedUser(42L, "user@example.com", "hash", "USER"));

        assertThatThrownBy(() -> service.extractUsername(token + "changed"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsExpiredToken() {
        JwtService service = new JwtService(SECRET, -1_000);
        String token = service.generateToken(
                new AuthenticatedUser(42L, "user@example.com", "hash", "USER"));

        assertThatThrownBy(() -> service.extractUsername(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void rejectsWeakSecret() {
        assertThatThrownBy(() -> new JwtService("too-short", 60_000))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("32 bytes");
    }
}

