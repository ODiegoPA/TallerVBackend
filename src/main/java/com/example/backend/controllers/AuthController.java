package com.example.backend.controllers;

import com.example.backend.dto.user.*;
import com.example.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.ok(userService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(userService.login(req));
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest req) {
        return ResponseEntity.ok(userService.refresh(req));
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        return userService.me(auth);
    }

    @PostMapping("/usuarios")
    public List<UserDto> findUsersByRole(@RequestBody(required = false) RolRequestDto rol) {
        return userService.getAllUsersByRole(rol);
    }
}
