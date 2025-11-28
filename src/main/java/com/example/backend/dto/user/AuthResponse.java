package com.example.backend.dto.user;

public record AuthResponse(String token, String refreshToken, String email, String nombre, String rol) {}
