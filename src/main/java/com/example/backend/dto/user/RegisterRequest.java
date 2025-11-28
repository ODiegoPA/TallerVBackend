package com.example.backend.dto.user;

public record RegisterRequest(
        String nombre,
        String apellido,
        String email,
        String password,
        String telefono,
        String rol
) {}
