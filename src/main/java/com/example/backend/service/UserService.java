// src/main/java/com/example/backend/service/UserService.java
package com.example.backend.service;

import com.example.backend.dto.user.*;
import com.example.backend.models.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtService;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        User u = new User();
        u.setNombre(req.nombre());
        u.setApellido(req.apellido());
        u.setEmail(req.email());
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setTelefono(req.telefono());
        u.setRol(req.rol());
        u.setCodigo(generateAlumnoCodeUnique());

        userRepository.save(u);

        String access = jwtService.generateToken(UserPrincipal.of(u));
        String refresh = jwtService.generateRefreshToken(u.getEmail());
        return new AuthResponse(access, refresh, u.getEmail(), u.getNombre(), u.getRol());
    }

    // LOGIN
    public AuthResponse login(LoginRequest req) {
        User u = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(req.password(), u.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String access = jwtService.generateToken(UserPrincipal.of(u));
        String refresh = jwtService.generateRefreshToken(u.getEmail());
        return new AuthResponse(access, refresh, u.getEmail(), u.getNombre(), u.getRol());
    }

    // REFRESH
    public AuthResponse refresh(RefreshRequest req) {
        String username;
        try {
            username = jwtService.extractUsernameFromRefresh(req.refreshToken());
        } catch (JwtException e) {
            throw new BadCredentialsException("Refresh token inválido");
        }

        User u = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!jwtService.isRefreshValid(req.refreshToken(), u.getEmail())) {
            throw new BadCredentialsException("Refresh token inválido o expirado");
        }

        String newAccess = jwtService.generateToken(UserPrincipal.of(u));
        String newRefresh = jwtService.generateRefreshToken(u.getEmail());
        return new AuthResponse(newAccess, newRefresh, u.getEmail(), u.getNombre(), u.getRol());
    }
    //ME
    public Map<String, Object> me(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        List<String> authorities = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Map.of(
                "name", auth.getName(),
                "authorities", authorities
        );
    }
    public List<UserDto> getAllUsersByRole(RolRequestDto req) {
        String role;
        if (req == null) {
            role = null;
        } else {
            role = req.rol();
        }
        if (role == null || role.isBlank()) {
            List<User> users = userRepository.findAll();
            return users.stream().map(this::toDto).toList();
        }
        List<User> users = userRepository.findAllByRol(role);
        return users.stream().map(this::toDto).toList();
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return UserPrincipal.of(u);
    }

    private String generateAlumnoCodeUnique() {
        for (int i = 0; i < 20; i++) {
            String candidate = String.format("%07d",
                    ThreadLocalRandom.current().nextInt(0, 10_000_000));
            if (!userRepository.existsByCodigo(candidate)) return candidate;
        }
        throw new IllegalStateException("No se pudo generar un código único para el alumno.");
    }

    private record UserPrincipal(String username, String password, String role) implements UserDetails {
        static UserPrincipal of(User u) {
            String role = (u.getRol() == null || u.getRol().isBlank()) ? "Estudiante" : u.getRol();
            return new UserPrincipal(u.getEmail(), u.getPassword(), role);
        }
        @Override public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority("ROLE_" + role));
        }
        @Override public String getPassword() { return password; }
        @Override public String getUsername() { return username; }
        @Override public boolean isAccountNonExpired() { return true; }
        @Override public boolean isAccountNonLocked() { return true; }
        @Override public boolean isCredentialsNonExpired() { return true; }
        @Override public boolean isEnabled() { return true; }
    }

    private UserDto toDto(User u) {
        return new UserDto(
                u.getId(),
                u.getNombre(),
                u.getApellido(),
                u.getEmail(),
                u.getTelefono(),
                u.getRol(),
                u.getCodigo()
        );
    }
}
