package com.example.backend.dto.semestre;

import java.time.LocalDate;

public record SemestreLiteDto(Long id, String nombre, LocalDate fechaInicio, LocalDate fechaFin) {
}
