package com.example.backend;

import com.example.backend.security.JwtService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails user;

    @BeforeEach
    void setup() {
        // Secrets >= 32 chars
        String accessSecret = "ACCESS_SECRET_123456789012345678901234";
        String refreshSecret = "REFRESH_SECRET_1234567890123456789012";
        // expiraciones cortas para tests
        jwtService = new JwtService(accessSecret, 2000L, refreshSecret, 3000L);
        user = new User("user@example.com", "pwd", List.of(new SimpleGrantedAuthority("ROLE_Student")));
    }

    @Test
    @DisplayName("generateToken incluye subject y claim role")
    void generateToken_incluyeClaims() {
        String token = jwtService.generateToken(user);
        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("user@example.com");
        assertThat(jwtService.extractClaim(token, "role")).isEqualTo("ROLE_Student");
    }

    @Test
    void extractUsername_lanzaSiTokenInvalido() {
        assertThatThrownBy(() -> jwtService.extractUsername("token.mal.formado"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void isAccessValid_trueConTokenValido() {
        String token = jwtService.generateToken(user);
        assertThat(jwtService.isAccessValid(token, user)).isTrue();
    }

    @Test
    void isAccessValid_lanzaSiExpirado() throws Exception {
        // expiración ultra corta
        String accessSecret = "ACCESS_SECRET_123456789012345678901234";
        String refreshSecret = "REFRESH_SECRET_1234567890123456789012";
        JwtService corto = new JwtService(accessSecret, 5L, refreshSecret, 1000L);
        String token = corto.generateToken(user);
        TimeUnit.MILLISECONDS.sleep(10); // dejar expirar
        assertThatThrownBy(() -> corto.isAccessValid(token, user))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void generateRefreshToken_y_validaciones() {
        String refresh = jwtService.generateRefreshToken(user.getUsername());
        assertThat(refresh).isNotBlank();
        assertThat(jwtService.extractUsernameFromRefresh(refresh)).isEqualTo("user@example.com");
        assertThat(jwtService.isRefreshValid(refresh, user.getUsername())).isTrue();
    }

    @Test
    void isRefreshValid_lanzaSiTokenFirmadoConOtraClave() {
        // Usar access token (firmado con accessKey) para validar refresh -> debe lanzar excepción de firma
        String access = jwtService.generateToken(user);
        assertThatThrownBy(() -> jwtService.isRefreshValid(access, user.getUsername()))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void isRefreshValid_falseSiUsernameDistinto() {
        String refresh = jwtService.generateRefreshToken(user.getUsername());
        assertThat(jwtService.isRefreshValid(refresh, "otro@example.com")).isFalse();
    }

    @Test
    void constructor_lanzaSiSecretCorto() {
        assertThatThrownBy(() -> new JwtService("abc", 1000L, "REFRESH_SECRET_1234567890123456789012", 2000L))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new JwtService("ACCESS_SECRET_123456789012345678901234", 1000L, "xyz", 2000L))
                .isInstanceOf(IllegalStateException.class);
    }
}
