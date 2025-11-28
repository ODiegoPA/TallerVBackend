package com.example.backend.dto.nota;

public record NotaRequestDto ( Long matriculacionId, Long evaluacionId, Integer ponderacion, Double calificacion) {
}
