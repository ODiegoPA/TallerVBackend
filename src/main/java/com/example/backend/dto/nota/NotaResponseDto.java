package com.example.backend.dto.nota;

import com.example.backend.dto.evaluacion.EvaluacionLiteDto;

public record NotaResponseDto (Long id, String evaluacion, Integer ponderacion, Double calificacion ){}
