package com.example.backend.dto.semestre;

import java.time.LocalDate;

public record SemestreRequestDto (String nombre, Long gestionId, LocalDate fechaInicio, LocalDate fechaFin) {
}
