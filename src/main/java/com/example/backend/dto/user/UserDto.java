package com.example.backend.dto.user;

public record UserDto(Long id, String nombre, String apellido, String email, String telefono, String rol, String codigo) {}
