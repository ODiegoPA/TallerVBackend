package com.example.backend.dto.semestre;

import com.example.backend.dto.gestion.GestionLiteDto;
import com.example.backend.models.Gestion;

import java.time.LocalDate;

public record SemestreResponseDto(Long id, String nombre, GestionLiteDto gestion, LocalDate fechaInicio, LocalDate fechaFin) {
}
